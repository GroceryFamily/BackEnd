package GroceryFamily.GrocerySis.txt;

import GroceryFamily.GroceryElders.api.client.ProductAPIClient;
import GroceryFamily.GroceryElders.domain.Detail;
import GroceryFamily.GroceryElders.domain.Namespace;
import GroceryFamily.GrocerySis.txt.dataset.OFFProductDataset;
import GroceryFamily.GrocerySis.txt.model.LabelSeeker;
import GroceryFamily.GrocerySis.txt.model.Labeled;
import GroceryFamily.GrocerySis.txt.model.MeansBuilder;
import GroceryFamily.GrocerySis.txt.model.Unlabeled;
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
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Component
@Profile("txt")
class TXTRunner implements CommandLineRunner {
    private final OFFProductDataset dataset;
    private final Labeled documents;
    private final Unlabeled unlabeled;
    private final ProductAPIClient client;

    TXTRunner(OFFProductDataset dataset, Labeled documents, Unlabeled unlabeled, ProductAPIClient client) {
        this.dataset = dataset;
        this.documents = documents;
        this.unlabeled = unlabeled;
        this.client = client;
    }

    @Override
    public void run(String... args) throws Exception {
        /*
        AtomicInteger no = new AtomicInteger();
        client.listAll().forEach(product -> System.out.println(no.incrementAndGet() + ": " + product));
         */

        client.listAll().forEach(product -> {
//            if (product.categories.values().stream().anyMatch("milk"::equalsIgnoreCase)) {
//                if (product.brand().isPresent() && product.brand().get().equalsIgnoreCase("alma")) {
            documents.add(product);
//                }
//            }
//            unlabeled.add(product);
        });

//        var prismaProductEANs = prismaProductEANs();
//        log.info("Fetched {} PRISMA product EANs", prismaProductEANs.size());
//
//        var offProducts = new ArrayList<OFFProduct>();
//        dataset.read(product -> {
//            if (prismaProductEANs.contains(product.code.value)) {
//                offProducts.add(product);
//            }
//        }, true);
//        log.info("Fetched {} OFF products", offProducts.size());
//        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(offProducts));


//        var offTotal = new AtomicInteger();
//        dataset.read(product -> {
//            offTotal.incrementAndGet();
//            if (isBlank(product.name)) return;
//            if (product.code.type == Code.Type.UNKNOWN) return;
//            labeled.add(product);
//        }, true);
//        log.info("Read {} products, created {} labels", offTotal, labeled.size);
//
//        var prismaTotal = new AtomicInteger();
//        client.listAll().forEach(product -> {
//            if (!Namespace.PRISMA.equals(product.namespace)) return;
//            prismaTotal.incrementAndGet();
//            unlabeled.add(product);
//        });
//        log.info("Created {} prisma unlabeled products", prismaTotal);
//
//
        var rimiParagraphVectors = makeParagraphVectors(Namespace.RIMI);
        checkUnlabeledData(Namespace.PRISMA, Namespace.RIMI, rimiParagraphVectors);
//        var prismaParagraphVectors = makeParagraphVectors(Namespace.PRISMA);
//        checkUnlabeledData(Namespace.RIMI, Namespace.PRISMA, prismaParagraphVectors);
    }

    private Set<String> prismaProductEANs() {
        return client
                .listAll()
                .filter(product -> Namespace.PRISMA.equals(product.namespace))
                .map(product -> product.details.get(Detail.EAN))
                .filter(Objects::nonNull)
                .collect(toSet());
    }

    LabelAwareIterator iterator(String namespace) {
        var resource = Paths.get("data/labeled/" + namespace);
        return new FileLabelAwareIterator.Builder().addSourceFolder(resource.toFile()).build();
    }

//    LabelAwareIterator unlabeledIterator(String namespace) {
//        var unClassifiedResource = Paths.get("data/unlabeled/" + namespace);
//        return new FileLabelAwareIterator.Builder().addSourceFolder(unClassifiedResource.toFile()).build();
//    }

    TokenizerFactory tokenizerFactory() {
        var tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
        return tokenizerFactory;
    }

    ParagraphVectors makeParagraphVectors(String namespace) {
        // ParagraphVectors training configuration
        var paragraphVectors = new ParagraphVectors
                .Builder()
                .learningRate(0.025)
                .minLearningRate(0.001)
                .batchSize(1000)
                .epochs(20)
                .iterate(iterator(namespace))
                .trainWordVectors(true)
                .tokenizerFactory(tokenizerFactory())
                .build();

        // Start model training
        paragraphVectors.fit();
        return paragraphVectors;
    }

    void checkUnlabeledData(String unlabeledNamespace, String labeledNamespace, ParagraphVectors paragraphVectors) throws IOException {
     /*
      Now we'll iterate over unlabeled data, and check which label it could be assigned to
      Please note: for many domains it's normal to have 1 document fall into few labels at once,
      with different "weight" for each.
     */
        var unClassifiedIterator = iterator(unlabeledNamespace);
        MeansBuilder meansBuilder = new MeansBuilder((InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable(), tokenizerFactory());
        LabelSeeker seeker = new LabelSeeker(iterator(labeledNamespace).getLabelsSource().getLabels(), (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable());

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
                var unclDoc = client.get(unlabeledNamespace + "::" + document.getLabels().get(0));
//                log.info("Document '" + document.getLabels() + "' falls into the following categories: ");
                log.info("Document '" + unclDoc.url + "' falls into the following categories: ");
                var maxScores = scores.stream().sorted(comparing(Pair::getSecond, reverseOrder())).limit(3).toList();
                for (var maxScore : maxScores) {
                    var clDoc = client.get(labeledNamespace + "::" + maxScore.getFirst());
                    log.info("        " + clDoc.url + ": " + maxScore.getSecond());
                }
//                var clDoc = client.get(labeledNamespace + "::" + maxScores.getFirst());
//                log.info("        " + maxScore.getFirst() + ": " + maxScore.getSecond());
//                log.info("        " + clDoc.url + ": " + maxScores.getSecond());
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
}