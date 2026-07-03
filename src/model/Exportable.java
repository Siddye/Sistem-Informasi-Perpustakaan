package model;

import java.io.IOException;

/**
 * Interface kontrak untuk entity yang dapat diekspor ke file (CSV/teks).
 */
public interface Exportable {
    String toCSV();
    void exportToFile(String filePath) throws IOException;
}
