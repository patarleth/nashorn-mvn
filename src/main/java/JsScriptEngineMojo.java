import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.StringReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * run javascript as a mojo
 *
 */
@Mojo( name = "jsmojo")
public class JsScriptEngineMojo extends AbstractMojo {
    /**
     * script text
     */
    @Parameter( property = "jsmojo.scripttext", defaultValue = "java.lang.System.out.println('hello');"  )
    private String scripttext = null;

    /**
     * script filename
     */
    @Parameter( property = "jsmojo.scriptfile" )
    private String scriptfile = null;

    public void execute() throws MojoExecutionException
    {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("nashorn");
        if(engine == null) {
            System.out.println("nashorn not found defaulting to js");
            engine = manager.getEngineByName("js");
        }
        engine.put("scriptengine", engine);
        Reader reader;
        try {
            if( scriptfile != null ) {
                File f = new File(scriptfile);
                if( f.exists() ) {
                    reader = new FileReader(f);
                } else {
                    reader = new StringReader(scripttext);
                }
            }
            else {
                reader = new StringReader(scripttext);
            }
            engine.eval(reader);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
