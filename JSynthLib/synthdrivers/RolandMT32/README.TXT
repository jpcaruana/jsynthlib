README.TXT file for the Roland MT-32 Driver 0.3 for 
JSynthLib 0.19 beta (0.20-alpha).

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


