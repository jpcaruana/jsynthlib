package org.jsynthlib.editorbuilder;
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
