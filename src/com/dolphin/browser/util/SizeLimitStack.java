/*******************************************************************************
 *
 *    Copyright (c) Dolphin Browser
 *
 * *    Dolphin Browser HD
 *    
 *    SizeLimitStack
 *    TODO File description or class description.
 *
 *    @author: derron
 *    @since:  Aug 3, 2010
 *    @version: 1.0
 *
 ******************************************************************************/
package com.dolphin.browser.util;

import java.util.LinkedList;

/**
 * SizeLimitStack of TunnyBrowser.If the count of pushed items is large than the Max size,then the oldest item will be removed.
 * @author derron
 *
 */
public class SizeLimitStack<E> extends LinkedList<E> {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6221278960628288830L;
	private final int mMaxSize;
	public SizeLimitStack(int capacity) {
		super();
		mMaxSize = capacity;
	}
	
	public int getMaxSize(){
		return mMaxSize;
	}
	
	/**
     * Returns whether the stack is empty or not.
     *
     * @return {@code true} if the stack is empty, {@code false} otherwise.
     */
    public boolean empty() {
        return size() == 0;
    }

    /**
     * Gets and removes the element at the head of the queue, or returns null if there is no element in the queue.
     *
     * @return the element at the head of the queue or null if there is no element in the queue. 
     */
    public E pop() {
        return poll();
    }

    /**
     * Pushes the specified object onto the top of the stack.
     *
     * @param object
     *            The object to be added on top of the stack.
     * @return the object argument.
     * @see #peek
     * @see #pop
     */
    public void push(E object) {
        addFirst(object);
        if(size() > mMaxSize){
        	removeLast();
        }
    }
}
