package ua.com.shtramak.sqlcmd.utils;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.io.IOException;
import java.util.Collection;

public class TableFormatter {
    public static <T> String formattedTableRow(Collection<T> data, int numOfColumns) throws IOException {
        //Turning off logger used in StreamingTable
        Logger.getLogger("ac.biu.nlp.nlp.engineml").setLevel(Level.OFF);
        Logger.getLogger("org.BIU.utils.logging.ExperimentLogger").setLevel(Level.OFF);
        Logger.getRootLogger().setLevel(Level.OFF);

        CellStyle cs = new CellStyle(CellStyle.HorizontalAlign.center, CellStyle.AbbreviationStyle.crop,
                CellStyle.NullStyle.emptyString);
        Table table = new Table(numOfColumns, BorderStyle.CLASSIC_COMPATIBLE_WIDE, ShownBorders.ALL, false, "");

        for (T rowElement:data){
            table.addCell(rowElement.toString());
        }

        return table.render();
    }
}
