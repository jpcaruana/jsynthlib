package org.jsynthlib.editorbuilder;
/*
 * Copyright 2002 Rib Rdb (TM). All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 * 
 * Neither the name of Rib Rdb (TM) or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. RIB RDB AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL RIB RDB OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF RIB RDB HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */
public class QuickSort
{
  private Comparable [] userArray;
  public static void sort( Comparable [] userArray )
  {
    QuickSort h = new QuickSort();
    h.userArray = userArray;
    if( h.isAlreadySorted() )
      return;
    h.quicksort( 0, userArray.length-1 );
    return;
  }

  private void quicksort( int p, int r )
  {
    if ( p < r ) {
      int q = partition( p, r );
      if ( q == r )
        q--;
      quicksort( p, q );
      quicksort( q+1, r );
    }
  }

  private int partition( int lo, int hi )
  {
    Comparable pivot = userArray[lo];
    while ( true ) {
      while ( userArray[hi].compareTo(pivot) >= 0 && lo < hi )
        hi--;
      while ( userArray[lo].compareTo(pivot) < 0 && lo < hi )
        lo++;
      if (lo < hi) {
        Comparable T = userArray[lo];
        userArray[lo] = userArray[hi];
        userArray[hi] = T;
        }
      else return hi;
    }
  }

  private boolean isAlreadySorted()
  {
    for ( int i=1; i<userArray.length; i++ )
      if ( userArray[i].compareTo(userArray[i-1]) < 0 )
	return false;
   return true;
  }
}
