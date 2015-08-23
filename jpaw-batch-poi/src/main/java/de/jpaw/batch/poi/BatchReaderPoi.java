package de.jpaw.batch.poi;

import java.io.FileInputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;

import de.jpaw.batch.api.BatchMainCallback;
import de.jpaw.batch.api.BatchReader;
import de.jpaw.batch.api.Contributor;

public class BatchReaderPoi implements Contributor, BatchReader<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchReaderPoi.class);
    private String filename = null;
    private String sheetName = null;
    protected int skip = 0;
    protected int maxRecords = 0;
    protected String delimiter = ";";
    protected int firstCol = 1;
    protected int lastCol = 0;      // if 0: export until an empty field has been encountered
    
    protected Workbook xls = null;
    protected Sheet sheet = null;
    
    public String getFilename() {
        return filename;
    }

    @Override
    public void addCommandlineParameters(JSAP params) throws Exception {
        params.registerParameter(new FlaggedOption("in",    JSAP.STRING_PARSER, null, JSAP.REQUIRED, 'i', "in", "input filename (extensions .xls and .xlsx are understood)"));
        params.registerParameter(new FlaggedOption("sheet", JSAP.STRING_PARSER, null, JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "sheet", "name of the sheet to use (by default, uses the first one)"));
        params.registerParameter(new FlaggedOption("skip",   JSAP.INTEGER_PARSER, "0", JSAP.NOT_REQUIRED, 's', "skip", "number of input rows to skip"));
        params.registerParameter(new FlaggedOption("maxnum", JSAP.INTEGER_PARSER, "999999999", JSAP.NOT_REQUIRED, 'm', "num", "maximum number of records to process"));
        params.registerParameter(new FlaggedOption("delimiter", JSAP.STRING_PARSER, delimiter, JSAP.NOT_REQUIRED, 'd', "delimiter", "delimiter character (usually ; or : or the pipe character)"));
        params.registerParameter(new FlaggedOption("first", JSAP.INTEGER_PARSER, "1", JSAP.NOT_REQUIRED, 'f', "first", "first column to export"));
        params.registerParameter(new FlaggedOption("last",  JSAP.INTEGER_PARSER, "0", JSAP.NOT_REQUIRED, 'l', "last", "last column to export"));
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
        case Cell.CELL_TYPE_STRING:
            return cell.getRichStringCellValue().getString();
        case Cell.CELL_TYPE_NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toString();
            } else {
                return Double.toString(cell.getNumericCellValue());
            }
        case Cell.CELL_TYPE_BOOLEAN:
            return Boolean.toString(cell.getBooleanCellValue());
        case Cell.CELL_TYPE_FORMULA:
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
            Row row = sheet.getRow(++lineNo);  
            Cell cell = row.getCell(firstCol - 1, Row.RETURN_BLANK_AS_NULL);
            if (cell == null)     // get the indicator line
                break;   // end of data
            buff.append(asString(cell));
            int last = lastCol == 0 ? row.getLastCellNum() : lastCol;
            for (int i = firstCol - 1; i < last; ++i) {
                buff.append(delimiter);
                buff.append(asString(row.getCell(i, Row.RETURN_BLANK_AS_NULL)));
            }
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
