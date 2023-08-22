package GroceryFamily.GrocerySis.dataset.io.progress;

import io.github.antivoland.cpb.ConsoleProgressBar;

public interface ProgressBarFactory {
    ProgressBar bar(long size);

    ProgressBarFactory CONSOLE = size -> {
        //noinspection resource
        var bar = new ConsoleProgressBar(size);
        return new ProgressBar() {
            @Override
            public void stepBy(long delta) {
                bar.stepBy(delta);
            }

            @Override
            public void close() {
                bar.close();
            }
        };
    };

    ProgressBarFactory DUMMY = size -> new ProgressBar() {
        @Override
        public void stepBy(long delta) {
            // do nothing
        }

        @Override
        public void close() {
            // do nothing
        }
    };
}