package com.raxim.myscoutee.algo;

import java.util.Iterator;

import com.raxim.myscoutee.algo.dto.CGroup;
import com.raxim.myscoutee.algo.dto.Range;

public class BCTree implements Iterable<CGroup> {

    private final CTree cTree;
    private final Range range;

    public BCTree(CTree cTree, Range range) {
        this.cTree = cTree;
        this.range = range;
    }

    @Override
    public Iterator<CGroup> iterator() {
        return new BCTreeIterator((CTreeIterator) this.cTree.iterator(), range);
    }
}
