var dontDoThis = new Packages.org.json.JSONObject();
dontDoThis.put('dont_do', 'this');
java.lang.System.out.println( dontDoThis.toString() );

var doThis = { 'hello' : 'nurse' };
java.lang.System.out.println( JSON.stringify(doThis, null,2) );