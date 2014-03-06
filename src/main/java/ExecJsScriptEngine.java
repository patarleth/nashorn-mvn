import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.StringReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ExecJsScriptEngine {
    public static void main(String[] args) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        engine.put("scriptengine", engine);
        Reader reader;
        for( int i = 0; i < args.length; i++ ) {
            String arg = args[i].trim();
            File f = new File(arg);
            try {
                if( f.exists() ) {
                    reader = new FileReader(f);
                } else {
                    reader = new StringReader(arg);
                }
                engine.eval(reader);
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
