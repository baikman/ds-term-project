Underflow(node) {
    if (lxfer possible) lxfer;
    else if (rxfer possible) rxfer;
    else if (lfusion possible) lfusion;
    else rfusion;
}

xfer possible if sib > 1 item
fusion if sib == 1

wcit(node) {
    parent = node.parent;
    for (int i = 0; i < parent.numkids(); i++) {
        if (parent.kid(i) == pos) return i;
    }
    throw new customError("Hi");
}

/**
     * What Child is This?
     * @param child to be searched for
     * @return int corresponding with the child
     */
    public int WCIT(TFNode child) {
        TFNode parent = child.getParent();
        for (int i = 0; i < parent.getNumItems(); i++) {
            if (parent.getChild(i) == child) return i;
        }
        throw new RuntimeException("Some error; could not get child index. Clarify this later.");
    }

// tfnode already has a shifting remove