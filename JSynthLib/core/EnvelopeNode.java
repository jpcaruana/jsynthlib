//This is a datatype used by EnvelopeWidget. It stores information about a single node (point) in the Widget 
package core;

public class EnvelopeNode
{
  public final static int SAME=5000;
  int minX; int minY; int maxX; int maxY; ParamModel ofsX; ParamModel ofsY;
  SysexSender senderX; SysexSender senderY;
  boolean invertX;int baseY;
  String nameX; String nameY;
  public EnvelopeNode(int minx, int maxx,ParamModel ofsx, int miny, int maxy, ParamModel ofsy,int basey,boolean invertx, SysexSender x, SysexSender y,String namex, String namey)
   { baseY=basey;minX=minx;maxX=maxx;minY=miny;maxY=maxy;ofsX=ofsx;ofsY=ofsy;senderX=x;senderY=y;nameX=namex;nameY=namey;invertX=invertx;}
   
 
}
/* When Constructing an Envelope Node, here's the meaning of the parameters passed to the constructor:
minx- The minimum value permitted by the Synth-Parameter which rides the X-Axis of the Node
maxx -The maximum value   ""            ""                        ""               ""
miny-      minumum        ""            ""                        ""     Y-Axis    ""
maxy- The  maximum        ""            ""                        ""       ""      ""
ofsx and ofsy- For the X and Y parameters-- this is the ParameterModel which provides reading/writing abilities to the
sysex data representing the parameter
basey- Sometimes you don't want Zero on a Y-access-riding-parameter to be all the way down at the bottom. This gives it a
little bit of rise. BaseY will be added to all Y values. (This doesn't change the function of the envelopeWidget, but makes
it look nicer and possibly be more intuitive to use
invertx- sometimes on an riding attribute 0 is the fastest, other times its the slowest. THis allows you to choose
x & y- The objects which send sysex messages to the synths when the Node is moved
namex, name y, the names of the parameters riding each access
**Using nulls for the Models and Senders and setting min to max means that a node is stationary on that access and has no
related parameter
**Using EnvelopeNode.SAME for MinY-MaxY means that the height remains at whatever the previous node was at
*/
