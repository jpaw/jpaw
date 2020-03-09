package de.jpaw.batch.poi;

import java.io.FileInputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;

import de.jpaw.batch.api.BatchFileReader;
import de.jpaw.batch.api.BatchMainCallback;
import de.jpaw.batch.api.Contributor;

public class BatchReaderPoi implements Contributor, BatchFileReader<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchReaderPoi.class);
    private String filename = null;
    private String sheetName = null;
    protected int skip = 0;
    protected int maxRecords = 0;
    protected String delimiter = ";";
    protected int firstCol = 1;
    protected int lastCol = 0;      // if 0: export until an empty field has been encountered
    protected int whileCol = 0;      // if 0: export until an empty field has been encountered

    protected Workbook xls = null;
    protected Sheet sheet = null;

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public int getSkip() {
        return skip;
    }

    @Override
    public int getMaxRecords() {
        return maxRecords;
    }

    @Override
    public String getEncoding() {
        return "UTF-8";     // no variable encoding for Excel
    }

    @Override
    public void addCommandlineParameters(JSAP params) throws Exception {
        params.registerParameter(new FlaggedOption("in",    JSAP.STRING_PARSER, null, JSAP.REQUIRED, 'i', "in", "input filename (extensions .xls and .xlsx are understood)"));
        params.registerParameter(new FlaggedOption("sheet", JSAP.STRING_PARSER, null, JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "sheet", "name of the sheet to use (by default, uses the first one)"));
        params.registerParameter(new FlaggedOption("skip",   JSAP.INTEGER_PARSER, "0", JSAP.NOT_REQUIRED, 's', "skip", "number of input rows to skip"));
        params.registerParameter(new FlaggedOption("maxnum", JSAP.INTEGER_PARSER, "65535", JSAP.NOT_REQUIRED, 'm', "num", "maximum number of records to process"));
        params.registerParameter(new FlaggedOption("delimiter", JSAP.STRING_PARSER, delimiter, JSAP.NOT_REQUIRED, 'd', "delimiter", "delimiter character (usually ; or : or the pipe character)"));
        params.registerParameter(new FlaggedOption("first", JSAP.INTEGER_PARSER, "1", JSAP.NOT_REQUIRED, 'f', "first", "first column to export"));
        params.registerParameter(new FlaggedOption("last",  JSAP.INTEGER_PARSER, "0", JSAP.NOT_REQUIRED, 'l', "last", "last column to export"));
        params.registerParameter(new FlaggedOption("while", JSAP.INTEGER_PARSER, "0", JSAP.NOT_REQUIRED, 'w', "while", "process rows while this column is not empty"));
    }


    @Override
    public void evalCommandlineParameters(JSAPResult params) throws Exception {
        filename   = params.getString("in");
        sheetName  = params.getString("sheet");
        delimiter  = params.getString("delimiter");
        skip       = params.getInt("skip");
        maxRecords = params.getInt("maxnum");
        firstCol   = params.getInt("first");
        lastCol    = params.getInt("last");
        whileCol   = params.getInt("while");

        if (filename.endsWith(".xls")) {
            xls = new HSSFWorkbook(new FileInputStream(filename));
        } else if (filename.endsWith(".xlsx")) {
            xls = new XSSFWorkbook(filename);
        } else {
            LOGGER.error("input file name {} must end with either .xls or .xlsx", filename);
            System.exit(1);
        }

        if (sheetName == null) {
            sheet = xls.getSheetAt(0);
        } else {
            sheet = xls.getSheet(sheetName);
            if (sheet == null) {
                LOGGER.error("No sheet with name {} exists in {}", sheetName, filename);
                System.exit(1);
            }
        }
    }

    protected String asString(Cell cell) {
        if (cell == null)
            return "";
        switch (cell.getCellType()) {
        case STRING:
            return cell.getRichStringCellValue().getString();
        case NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toString();
            } else {
                double d = cell.getNumericCellValue();
                if (Double.isFinite(d) && d == Math.floor(d)) {
                    // integral
                    return Long.toString(Double.valueOf(d).longValue());
                }
                return Double.toString(d);
            }
        case BOOLEAN:
            return Boolean.toString(cell.getBooleanCellValue());
        case FORMULA:
            return cell.getCellFormula();
        default:
            return "";
        }
    }

    @Override
    public void produceTo(BatchMainCallback<? super String> whereToPut) throws Exception {
        StringBuffer buff = new StringBuffer(4000);
        int lineNo = skip - 1;  // first row has index 0
        for (;;) {
            buff.setLength(0);

            // get row, stop if not present
            Row row = sheet.getRow(++lineNo);
            if (row == null)
                break;

            // break criteria if indicator column is empty
            if (whileCol > 0 && row.getCell(whileCol - 1, MissingCellPolicy.RETURN_BLANK_AS_NULL) == null)
                break;   // end of data

            int last = lastCol == 0 ? row.getLastCellNum() : lastCol;
            buff.append(asString(row.getCell(firstCol - 1, MissingCellPolicy.RETURN_BLANK_AS_NULL)));
            for (int i = firstCol; i < last; ++i) {
                buff.append(delimiter);
                buff.append(asString(row.getCell(i, MissingCellPolicy.RETURN_BLANK_AS_NULL)));
            }
            buff.append('\n');
            whereToPut.accept(buff.toString());
            if (--maxRecords == 0)
                break;
        }
    }

    @Override
    public void close() throws Exception {
        xls.close();
        // super.close();
    }
}
