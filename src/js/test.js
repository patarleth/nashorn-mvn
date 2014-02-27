var CollectionsAndFiles = new JavaImporter(
    java.util.Scanner,
    java.io.File,
    java.lang.System);

with (CollectionsAndFiles) {
    var cmd = System.getProperties().get('sun.java.command').split(' ');
    var filename = cmd[cmd.length-1];
    var file = new File(filename);
    var url = file.toURI().toURL().openConnection();
    var fileContents = (new Scanner(url.getInputStream())).useDelimiter("\\Z").next();
    
    System.out.println();
    System.out.println(fileContents);
    System.out.println();
    System.out.println('returns:');
    
    var dontDoThis = new Packages.org.json.JSONObject();
    dontDoThis.put('dont_do', 'this');
    java.lang.System.out.println( dontDoThis.toString() );
    
    var doThis = { 'hello' : 'nurse' };
    System.out.println( JSON.stringify(doThis, null,2) );
}