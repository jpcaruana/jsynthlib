/*
 * MyClassLoader.java
 *
 * Created on 12. September 1999, 00:30
 */
 

package core;
/** 
 *
 * @author  gerrit
 * @version 
 */
import java.io.*; 
import java.util.*; 

// This class loader uses an alternate directory for loading classes. 
// When a class is resolved, its class loader is expected to be able 
// to load any additional classes, but this loader doesn't want to have 
// to figure out where to find java.lang.Object, for instance, so it 
// uses Class.forName to locate classes that the system already knows 
// about. 

public class MyClassLoader extends ClassLoader 
{ 
String classDir; // root dir to load classes from 
Hashtable loadedClasses; // Classes that have been loaded 

public MyClassLoader(String classDir) 
{ 
this.classDir = classDir; 
loadedClasses = new Hashtable(); 
} 

public synchronized Class loadClass(String className, 
boolean resolve) throws ClassNotFoundException 
{ 
  //System.out.println("loadClass: "+className);
Class newClass =findLoadedClass(className);
if (newClass!=null) return newClass;

// If the class was in the loadedClasses table, we don't 
// have to load it again, but we better resolve it, just 
// in case. 
newClass=(Class) loadedClasses.get(className); 

if (newClass != null) 
{ 
if (resolve) // Should we resolve? 
{ 
resolveClass(newClass); 
} 
return newClass; 
} 

try { 
// Read in the class file 
byte[] classData = getClassData(className); 
// Define the new class 
newClass = defineClass(null,classData, 0, 
classData.length); 
} catch (IOException readError) { 

// Before we throw an exception, see if the system already knows 
// about this class 
try { 
newClass = findSystemClass(className); 
return newClass; 
} catch (Exception any) { 
throw new ClassNotFoundException(className); 
} 
} 

// Store the class in the table of loaded classes 
loadedClasses.put(className, newClass); 

// If we are supposed to resolve this class, do it 
if (resolve) 
{ 
resolveClass(newClass); 
} 

return newClass; 
} 

// This version of loadClass uses classDir as the root directory 
// for where to look for classes, it then opens up a read stream 
// and reads in the class file as-is. 

protected byte[] getClassData(String className) 
throws IOException 
{ 
// Rather than opening up a FileInputStream directly, we create 
// a File instance first so we can use the length method to 
// determine how big a buffer to allocate for the class 

File classFile = new File(classDir, className+".class"); 

byte[] classData = new byte[(int)classFile.length()]; 

// Now open up the input stream 
FileInputStream inFile = new FileInputStream(classFile); 

// Read in the class 
int length = inFile.read(classData); 

inFile.close(); 

return classData; 
} 
} 
