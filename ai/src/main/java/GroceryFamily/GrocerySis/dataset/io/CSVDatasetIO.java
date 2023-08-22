package GroceryFamily.GrocerySis.dataset.io;

import GroceryFamily.GrocerySis.dataset.io.progress.ProgressBar;
import GroceryFamily.GrocerySis.dataset.io.progress.ProgressBarFactory;
import lombok.Builder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.String.format;

@Builder
public class CSVDatasetIO {
    private final String separator;
    @Builder.Default
    private final ProgressBarFactory progressBarFactory = ProgressBarFactory.DUMMY;
    private final int skip;

    public void read(Path dataset, Consumer<String[]> handler) {
        try (var bar = progressBarFactory.bar(Files.size(dataset))) {
            try (var lines = lines(dataset, bar)) {
                lines.forEach(line -> handler.accept(line.split(separator)));
            }
        } catch (Exception e) {
            throw new DatasetIOException(format("Failed to read dataset '%s'", dataset), e);
        }
    }

    private Stream<String> lines(Path dataset, ProgressBar bar) throws IOException {
        //noinspection resource
        return Files
                .lines(dataset)
                .peek(line -> bar.stepBy(line.getBytes().length + 1))
                .skip(skip);
    }
}