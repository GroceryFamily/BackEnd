package GroceryFamily.GrocerySis;

import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GrocerySis.dataset.OFFProductDataset;
import GroceryFamily.GrocerySis.model.*;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.text.documentiterator.FileLabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@SpringBootApplication
class GrocerySis implements CommandLineRunner {
    private final OFFProductDataset dataset;
    private final Labeled labeled;
    private final Unlabeled unlabeled;
    private final ProductAPIClient client;

    GrocerySis(OFFProductDataset dataset, Labeled labeled, Unlabeled unlabeled, ProductAPIClient client) {
        this.dataset = dataset;
        this.labeled = labeled;
        this.unlabeled = unlabeled;
        this.client = client;
    }

    @Override
    public void run(String... args) throws Exception {
        /*
        AtomicInteger no = new AtomicInteger();
        client.listAll().forEach(product -> System.out.println(no.incrementAndGet() + ": " + product));
         */

        var offTotal = new AtomicInteger();
        dataset.read(product -> {
            offTotal.incrementAndGet();
            if (isBlank(product.name)) return;
            if (product.code.type == Code.Type.UNKNOWN) return;
            labeled.add(product);
        }, true);
        log.info("Read {} products, created {} labels", offTotal, labeled.size);

        var prismaTotal = new AtomicInteger();
        client.listAll().forEach(product -> {
            if (!Namespace.PRISMA.equals(product.namespace)) return;
            prismaTotal.incrementAndGet();
            unlabeled.add(product);
        });
        log.info("Created {} prisma unlabeled products", prismaTotal);


        makeParagraphVectors();
        checkUnlabeledData();
    }

    ParagraphVectors paragraphVectors;
    LabelAwareIterator iterator;
    TokenizerFactory tokenizerFactory;

    void makeParagraphVectors() throws Exception {
        var resource = Paths.get("data/labeled");

        // build an iterator for our dataset
        iterator = new FileLabelAwareIterator.Builder().addSourceFolder(resource.toFile()).build();

        tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        // ParagraphVectors training configuration
        paragraphVectors = new ParagraphVectors.Builder().learningRate(0.025).minLearningRate(0.001).batchSize(1000).epochs(20).iterate(iterator).trainWordVectors(true).tokenizerFactory(tokenizerFactory).build();

        // Start model training
        paragraphVectors.fit();
    }

    void checkUnlabeledData() throws IOException {
      /*
      At this point we assume that we have model built and we can check
      which categories our unlabeled document falls into.
      So we'll start loading our unlabeled documents and checking them
     */
        var unClassifiedResource = Paths.get("data/unlabeled"); // todo: hardcode
        FileLabelAwareIterator unClassifiedIterator = new FileLabelAwareIterator.Builder().addSourceFolder(unClassifiedResource.toFile()).build();

     /*
      Now we'll iterate over unlabeled data, and check which label it could be assigned to
      Please note: for many domains it's normal to have 1 document fall into few labels at once,
      with different "weight" for each.
     */
        MeansBuilder meansBuilder = new MeansBuilder((InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable(), tokenizerFactory);
        LabelSeeker seeker = new LabelSeeker(iterator.getLabelsSource().getLabels(), (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable());

        while (unClassifiedIterator.hasNextDocument()) {
            try {
                LabelledDocument document = unClassifiedIterator.nextDocument();
                INDArray documentAsCentroid = meansBuilder.documentAsVector(document);
                List<Pair<String, Double>> scores = seeker.getScores(documentAsCentroid);

                 /*
                  please note, document.getLabel() is used just to show which document we're looking at now,
                  as a substitute for printing out the whole document name.
                  So, labels on these two documents are used like titles,
                  just to visualize our classification done properly
                 */
                log.info("Document '" + document.getLabels() + "' falls into the following categories: ");
                var maxScore = scores.stream().max(comparing(Pair::getSecond)).orElseThrow();
                    log.info("        " + maxScore.getFirst() + ": " + maxScore.getSecond());
//                for (Pair<String, Double> score : scores) {
//                    log.info("        " + score.getFirst() + ": " + score.getSecond());
//                }

//                var maxScore = scores.stream().max(comparing(Pair::getSecond)).orElseThrow();
//                var offers = Resource.linkedinOffers();
//                var txtStorage = Resource.textFiles("nlp", "classified", maxScore.getFirst());
//                var id = document.getLabels().stream().findFirst().orElseThrow();
//                txtStorage.save(id, offers.load(id).getDescription());
            } catch (Exception e) {
                log.warn("", e);
            }
        }

    }

    public static void main(String... args) {
        SpringApplication.run(GrocerySis.class, args);
    }
}