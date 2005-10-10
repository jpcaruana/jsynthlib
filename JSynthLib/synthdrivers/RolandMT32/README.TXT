README.TXT file for the Roland MT-32 Driver 0.10 for 
JSynthLib 0.20.

Changes:
- Stole the ComboBoxWidget with images idea from the JV80 
  editor by Sander Brandenburg.
- Changed filenamed of some images. Filenames containing an '='
  do not work in a jar file (no such problem from Eclipse).
- Fixed the Display Driver. Now you can specify your own text
  on the MT32 display. Reading the display contents doesn't work.
Bugs:
- bank functionality still not complete

Fred Jan Kraan
2005-10-06

0.9 readme text:
Changes:
- rename of files to reflect the MIDI implementation chart
- adding of bank functionality
Bugs:
- display driver/editor not working (will only be write only)
- writing of banks not possible.
- Send not working (but Store... is)

Fred Jan Kraan
2005-10-01


0.8 readme text:

This version is compatible with the JSynthLib 0.20.
Other changes are:
- minor changes in the partial images
- fixed the bug in the Rhythm Setup Driver
- preparation of the display driver
- preparation for a bank manager

Using the instructions presented here: 
http://ribrdb.com/jsl/eclipse.html, I checked out
the JSynthLib project into Eclipse 3.1, copied the newer 
Roland MT32 files and applied the changes described 
above.
No errors or warnings are detected from the MT32 files.

Fred Jan Kraan
2005-09-25


0.7 readme text:

The only difference is that the calculateChecksum method
is now 'protective static' instead of 'private'.

Alle other bugs are still present.

fjk, 2005-03-03


0.6 readme text:

The current version 0.6 can edit each retrieved Timbre Temp 
Area and Patch Temp Area, but not much more. The System Area 
can be retrieved and edited, but the Rhythm Setup is broken.
For the TTAs and PTAs retrieval and storage is possible, but 
data transfer from one TTA/PTA to another is not.

Some cosmetic changes are made, like the Partial Structure
graphics. I stole those from another editor and modified them.
 
Bugs: 
- The bankNum and PatchNum in the Schene table does not match 
the actual timbre/patch number. The philosophy of JSynthLib is 
that a patch shouldn't be aware of its origin. But the MT-32 
(probably other synths too) contains the origin within the
data retrieved from the synth. 
And typically, if you retrieve data from a certain timbre/patch
location for editing, you probably want to send changes to that
same location. 
Moving data from one location to another is also useful, but 
this is part of a bank manager, not an editor.
- Rhythm Setup retrieval is broken. The data is retrieved OK, 
but it doesn't show op in the Scene window. Creating a new set
does work.



Tools:
Editing and compilation is done with NetBeans IDE 3.5.
As something broke during installation the JavaDoc doesn't
work.
The development machine is a 1800MHz P4 running Win2k SP latest.
No testing is done on other machines.


Method of development:
The method I use is best described as "Interpolating 
Programming" which means almost all lines are copied 
from elsewhere in JSynthLib. I try to concentrate on 
what the driver should do and as little as possible on
JSynthLib internals or even Java syntax. This works ok
for most parts, but getting the layout right is a 
problem. The current status is the result of a long 
evolutionary path.

Plans:
As the MT-32 editor is just a proof of concept for the 
real target, a editor for the Lexicon LXP-5, not much
development is planned. Only when I get the idea 
something wil be very easy to add, I will do it. But 
feel free to ask or do it yourself :-)


Fred Jan Kraan
2004-07-26


0.5 Readme text:

The current version 0.5 can edit each retrieved Timbre Temp 
Area and Patch Temp Area, but not much more. The System Area 
can be retrieved and edited, but the Rhythm Setup is broken.
For the TTA and PTA retrieval and storage is possible, but 
data transfer from one TTA/PTA to another is not.

Some cosmetic changes are made, like the Partial Structure
graphics. I stole those from another editor and modified them.
 

Fred Jan Kraan
2004-07-24


0.4 Readme text:

Since 0.3 Drivers/Editors are added for the temporary
patch area's and System Area.
The Patch Temp editor has the same problem as the Timbre Temp 
editor. But improved insight might result in a fix for both.

Fred Jan Kraan
2004-07-22


0.3 Readme text:
This version is very like the previous and first release
0.2.
The differences are:
- Standard JSynthLib copyright header in all java files
- Cosmetic changes in the Partial panes. Now each Partial
  tab pane has three sub tab panes for the parameters. 
  In 0.2 there was only one tab pane level with 12 tabs.

Bugs:
Not much improvement here. All changes to the patch are 
send to the first Timbre Temp Area, regardless of origin.
As it is unclear to me how this should work, I can't fix 
anything. The unreleased version 0.1 which was developed
with 0.18 did work as expected for this aspect. 

Tools:
Editing and compilation is done with NetBeans IDE 3.5.

Method of development:
The method I use is best described as "Interpolating 
Programming" which means almost all lines are copied 
from elsewhere in JSynthLib. I try to concentrate on 
what the driver should do and as little as possible on
JSynthLib internals or even Java syntax. This works ok
for most parts, but getting the layout right is a 
problem. The current status is the result of a long 
evolutionary path.

Plans:
As the MT-32 editor is just a proof of concept for the 
real target, a editor for the Lexicon LXP-5, not much
development is planned. Only when I get the idea 
something wil be very easy to add, I will do it. But 
feel free to ask or do it yourself :-)

Fred Jan Kraan,
2004-07-20


