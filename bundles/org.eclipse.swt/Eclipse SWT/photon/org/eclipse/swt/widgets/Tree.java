package org.eclipse.swt.widgets;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import java.io.*;
import java.util.*;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved
 */
 
/** 
 * Displays a hierarchy of items that can be selected. 
 * Sub hierarchies can be expanded and collapsed.
 */
public /*final*/ class Tree extends SelectableItemWidget {
	// These constants are used internally for item hit test on mouse click
	private static final int ActionNone = 0;			// The mouse event was not handled
	private static final int ActionExpandCollapse = 1;	// Do an expand/collapse
	private static final int ActionSelect = 2;			// Select the item
	private static final int ActionCheck = 3;			// Toggle checked state of the item
	private static ImageData CollapsedImageData;		// collapsed sub tree image data. used to create an image at run time
	private static ImageData ExpandedImageData;			// expanded sub tree image data. used to create an image at run time
	static {
		initializeImageData();
	}
	
	private TreeRoots root;
	private TreeItem expandingItem;
	
	private Image collapsedImage;
	private Image expandedImage;

	// The following fields are needed for painting tree items
	final Color CONNECTOR_LINE_COLOR;					// Color constant used during painting. Can't keep this in TreeItem 
														// because we only need one instance per tree widget/display and can't 
														// have it static. Initialized in c'tor and freed in dispose();
	Rectangle hierarchyIndicatorRect = null;			// bounding rectangle of the hierarchy indication image (plus/minus)

/**
 * Create a new instance of the receiver with 'parent' 
 * as its parent widget.
 */
public Tree(Composite parent, int style) {
	super(parent, checkStyle (style));
	CONNECTOR_LINE_COLOR = new Color(getDisplay(), 170, 170, 170);	// Light gray;
}
/**
 * Add 'item' to the list of root items.
 * @param 'item' - the tree item that should be added as a root.
 * @param index - position that 'item' will be inserted at
 *	in the receiver.
 */
void addItem(TreeItem item, int index) {
	if (index < 0 || index > getItemCount()) {
		error(SWT.ERROR_INVALID_RANGE);
	}
	getRoot().add(item, index);
}
/**	 
* Adds the listener to receive events.
* <p>
*
* @param listener the listener
*
*/
public void addSelectionListener(SelectionListener listener) {
	if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	if (!isValidWidget ()) error (SWT.ERROR_WIDGET_DISPOSED);
	TypedListener typedListener;

	if (listener == null) {
		error(SWT.ERROR_NULL_ARGUMENT);
	}
	typedListener = new TypedListener(listener);	
	addListener(SWT.Selection, typedListener);
	addListener(SWT.DefaultSelection, typedListener);
}
/**	 
* Adds the listener to receive events.
* <p>
*
* @param listener the listener
*
*/
public void addTreeListener(TreeListener listener) {
	if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	if (!isValidWidget ()) error (SWT.ERROR_WIDGET_DISPOSED);
	TypedListener typedListener;

	if (listener == null) {
		error(SWT.ERROR_NULL_ARGUMENT);
	}
	typedListener = new TypedListener(listener);	
	addListener(SWT.Expand, typedListener);
	addListener(SWT.Collapse, typedListener);
}
/**
 * The SelectableItem 'item' has been added to the tree.
 * Prevent screen updates when 'item' is inserted due to an 
 * expand operation.
 * @param item - item that has been added to the receiver.
 */
void addedItem(SelectableItem item, int index) {
	super.addedItem(item, index);				
	redrawAfterModify(item, index);		// redraw plus/minus image, hierarchy lines
}
/**
 * Answer the y position of both the first child of 'item' and 
 * the item following the last child of 'item'.
 * Used to scroll items on expand/collapse.
 * @param item - TreeItem to use for calculating the y boundary 
 *	of child items.
 * @return Array - first element is the position of the first 
 *	child of 'item'. Second element is the position of the item 
 *	following the last child of 'item'.
 *	Both elements are -1 if 'item' is not a child of the receiver.
 */
int[] calculateChildrenYPos(TreeItem item) {
	int itemIndex = item.getVisibleIndex();
	int itemCount = item.getVisibleItemCount();
	int itemHeight = getItemHeight();
	int yPos;
	int[] yPosition = new int[] {-1, -1};

	if (itemIndex != -1) {
		itemIndex -= getTopIndex();															
		yPos = (itemIndex + itemCount + 1) * itemHeight;	// y position of the item following 
															// the last child of 'item'
		yPosition = new int[] {yPos - (itemCount * itemHeight), yPos};
	}
	return yPosition;
}
/**
 * Calculate the widest of the children of 'item'.
 * Items that are off screen and that may be scrolled into view are 
 * included in the calculation.
 * @param item - the tree item that was expanded
 */
void calculateWidestExpandingItem(TreeItem item) {
	int itemIndex = item.getVisibleIndex();
	int newMaximumItemWidth = getContentWidth();
	int stopIndex = itemIndex + item.getVisibleItemCount();

	for (int i = itemIndex + 1; i <= stopIndex; i++) {
		newMaximumItemWidth = Math.max(newMaximumItemWidth, getContentWidth(i));
	}
	setContentWidth(newMaximumItemWidth);
}
/**
 * Calculate the width of new items as they are scrolled into view.
 * Precondition: 
 * topIndex has already been set to the new index.
 * @param topIndexDifference - difference between old and new top 
 *	index.
 */
void calculateWidestScrolledItem(int topIndexDifference) {
	int visibleItemCount = getItemCountTruncated(getClientArea());	
	int newMaximumItemWidth = getContentWidth();
	int topIndex = getTopIndex();
	int stopIndex = topIndex;

	if (topIndexDifference < 0) {								// scrolled up?
		if (Math.abs(topIndexDifference) > visibleItemCount) {	// scrolled down more than one page (via quick thumb dragging)?
			topIndexDifference = visibleItemCount * -1;
		}
		for (int i = stopIndex - topIndexDifference; i >= stopIndex; i--) {	// check item width from old top index up to new one
			newMaximumItemWidth = Math.max(newMaximumItemWidth, getContentWidth(i));
		}
	}
	else
	if (topIndexDifference > 0) {								// scrolled down?
		if (topIndexDifference > visibleItemCount) {			// scrolled down more than one page (via quick thumb dragging)?
			topIndexDifference = visibleItemCount;
		}
		stopIndex += visibleItemCount;		
		for (int i = stopIndex - topIndexDifference; i < stopIndex; i++) {
			newMaximumItemWidth = Math.max(newMaximumItemWidth, getContentWidth(i));
		}
	}
	setContentWidth(newMaximumItemWidth);
}
/**
 * Calculate the maximum item width of all displayed items.
 */
void calculateWidestShowingItem() {
	TreeItem visibleItem;
	int newMaximumItemWidth = 0;
	int bottomIndex = getBottomIndex();
	int paintStopX;

	// add one to the loop end index because otherwise an item covered 
	// by the horizontal scroll bar would not be taken into acount and 
	// may become visible after this calculation. We're in trouble if
	// that item is wider than the client area.
	if (getHorizontalBar().getVisible() == true) {
		bottomIndex++;
	}
	for (int i = getTopIndex(); i < bottomIndex; i++) {
		visibleItem = getRoot().getVisibleItem(i);
		if (visibleItem != null) {
			paintStopX = visibleItem.getPaintStopX();
			newMaximumItemWidth = Math.max(newMaximumItemWidth, paintStopX);
		}
	}
	setContentWidth(newMaximumItemWidth);
}
static int checkStyle (int style) {
	return checkBits (style, SWT.SINGLE, SWT.MULTI, 0, 0, 0, 0);
}
protected void checkSubclass () {
	if (!isValidSubclass ()) error (SWT.ERROR_INVALID_SUBCLASS);
}
/**
 * Collapse the tree item identified by 'item' if it is not 
 * already collapsed. Move the selection to the parent item 
 * if one of the collapsed items is currently selected.
 * @param item - item that should be collapsed.
 * @param notifyListeners - 
 *	true=a Collapse event is sent 
 *	false=no event is sent
 */
void collapse(TreeItem item, boolean notifyListeners) {
	Event event;
	int itemIndex;
	
	if (item.getExpanded() == false) {
		return;
	}
	collapseNoRedraw(item);
	itemIndex = item.getVisibleIndex();
	if (itemIndex != -1) {						// if the item's parent is not collapsed (and the item is thus visible) do the screen updates
		item.redrawExpanded(itemIndex - getTopIndex());
		showSelectableItem(item);
		calculateVerticalScrollbar();
		calculateWidestShowingItem();
		claimRightFreeSpace();
		claimBottomFreeSpace();		
	}
	if (notifyListeners == true) {
		event = new Event();
		event.item = item;
		notifyListeners(SWT.Collapse, event);
	}
}

/**
 * Collapse the tree item identified by 'item' if it is not 
 * already collapsed. Move the selection to the parent item 
 * if one of the collapsed items is currently selected.
 * This method is used to hide the children if an item is deleted.
 * certain redraw and scroll operations are not needed for this 
 * case.
 * @param item - item that should be collapsed.
 */
void collapseNoRedraw(TreeItem item) {
	int itemIndex;
	
	if (item.getExpanded() == false) {
		return;
	}
	if (isSelectedItemCollapsing(item) == true) {
		deselectAllExcept(item);
		selectNotify(item);
		update();								// call update to make sure that new selection is 
												// drawn before items are collapsed (looks better)
	}
	scrollForCollapse(item);
	item.internalSetExpanded(false);
}

/**
 * Answer the size of the receiver needed to display all or 
 * the first 50 items whichever is less.
 */
public Point computeSize(int wHint, int hHint, boolean changed) {
	if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	if (!isValidWidget ()) error (SWT.ERROR_WIDGET_DISPOSED);
	Point size = super.computeSize(wHint, hHint, changed);
	GC gc;
	final int WidthCalculationCount = 50;		// calculate item width for the first couple of items only
	TreeRoots root = getRoot();
	TreeItem item;
	Image itemImage;
	String itemText;
	int width;
	int newItemWidth = 0;
		
	if (getContentWidth() == 0 && getItemCount() > 0) {
		gc = new GC(this);
		for (int i = 0; i < WidthCalculationCount; i++) {
			item = root.getVisibleItem(i);
			if (item == null) {
				break;											// no more items
			}
			itemImage = item.getImage();
			itemText = item.getText();
			width = 0;
			if (itemImage != null) {
				width += itemImage.getBounds().width;
			}
			if (itemText != null) {
				width += gc.stringExtent(itemText).x;
			}
			newItemWidth = Math.max(newItemWidth, width);
		}
		if (newItemWidth > 0) {
			size.x = newItemWidth;
		}		
		gc.dispose();
	}
	return size;
}
/**
 * Deselect all items of the receiver.
 */
public void deselectAll() {
	if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	if (!isValidWidget ()) error (SWT.ERROR_WIDGET_DISPOSED);

	getRoot().deselectAll();
	getSelectionVector().removeAllElements();
	redraw();
}
/**
 * Modifier Key		Action
 * None				Collapse the selected item if expanded. Select 
 * 					parent item if selected item is already 
 * 					collapsed and if it's not the root item.
 * Ctrl				super.doArrowLeft(int);
 * Shift			see None above
 * @param keyMask - the modifier key that was pressed
 */
void doArrowLeft(int keyMask) {
	TreeItem focusItem = (TreeItem) getLastFocus();
	TreeItem parentItem;

	if (focusItem == null) {
		return;
	}
	if (keyMask == SWT.CTRL) {
		super.doArrowLeft(keyMask);
	}
	else
	if (focusItem.getExpanded() == true) {			// collapse if expanded
		collapse(focusItem, true);
	}
	else
	if (focusItem.isRoot() == false) {				// go to the parent if there is one
		parentItem = focusItem.getParentItem();
		deselectAllExcept(parentItem);
		selectNotify(parentItem);
	}
}
/**
 * Modifier Key		Action
 * None				Expand selected item if collapsed. Select 
 * 					first child item if selected item is 
 *					already expanded and there is a child item.
 * Ctrl				super.doArrowRight(keyMask);
 * Shift			see None above
 * @param keyMask - the modifier key that was pressed
 */
void doArrowRight(int keyMask) {
	TreeItem focusItem = (TreeItem) getLastFocus();
	TreeItem childItem;

	if (focusItem == null) {
		return;
	}	
	if (keyMask == SWT.CTRL) {
		super.doArrowRight(keyMask);
	}
	else
	if (focusItem.isLeaf() == false) {
		if (focusItem.getExpanded() == false) {			// expand if collapsed
			expand(focusItem, true);
		} 
		else {											// go to the first child if there is one
			childItem = focusItem.getItems()[0];
			deselectAllExcept(childItem);
			selectNotify(childItem);
		}
	}
}
/**
 * Expand the selected item and all of its children.
 */
void doAsterix() {
	expandAll((TreeItem) getLastFocus());
}
/**
 * Free resources.
 */
void doDispose() {
	super.doDispose();	
	if (collapsedImage != null) {
		collapsedImage.dispose();
	}
	if (expandedImage != null) {
		expandedImage.dispose();
	}
	getRoot().dispose();
	CONNECTOR_LINE_COLOR.dispose();
	resetHierarchyIndicatorRect();
}
/**
 * Collapse the selected item if it is expanded.
 */
void doMinus() {
	TreeItem selectedItem = (TreeItem) getLastFocus();

	if (selectedItem != null) {
		collapse(selectedItem, true);
	}
}
/**
 * Expand the selected item if it is collapsed and if it 
 * has children.
 */
void doPlus() {
	TreeItem selectedItem = (TreeItem) getLastFocus();

	if (selectedItem != null && selectedItem.isLeaf() == false) {
		expand(selectedItem, true);
	}
}
/**
 * Expand the tree item identified by 'item' if it is not already 
 * expanded. Scroll the expanded items into view.
 * @param item - item that should be expanded
 * @param notifyListeners - 
 *	true=an Expand event is sent 
 *	false=no event is sent
 */
void expand(TreeItem item, boolean notifyListeners) {
	Event event = new Event();
	int indexFromTop;
	boolean nestedExpand = expandingItem != null;

	if (item.getExpanded() == true) {
		return;
	}
	if (nestedExpand == false) {
		setExpandingItem(item);
	}
	scrollForExpand(item);
	item.internalSetExpanded(true);
	if (notifyListeners == true) {
		event.item = item;
		notifyListeners(SWT.Expand, event);
	}
	// redraw hierarchy image
	item.redrawExpanded(item.getVisibleIndex() - getTopIndex());
	calculateVerticalScrollbar();
	if (nestedExpand == false && isVisible() == true) {
		// Save the index here because showSelectableItem may change it
		indexFromTop = item.getVisibleIndex() - getTopIndex();		
		showSelectableItem(item);				// make expanded item visible. Could be invisible if the expand was caused by a key press.		
		calculateWidestExpandingItem(item);
		scrollExpandedItemsIntoView(item);		
	}
	if (nestedExpand == false) {
		setExpandingItem(null);
	}
}
/**
 * Expand 'item' and all its children.
 */
void expandAll(TreeItem item) {
	TreeItem items[];

	if (item != null && item.isLeaf() == false) {
		expand(item, true);
		update();
		items = item.getItems(); 
		for (int i = 0; i < items.length; i++) {
			expandAll(items[i]);
		}
	}
}
/**
 * Answer the image that is used as a hierarchy indicator 
 * for a collapsed hierarchy.
 */
Image getCollapsedImage() {
	if (collapsedImage == null) {
		collapsedImage = new Image(getDisplay(), CollapsedImageData);
	}
	return collapsedImage;
}
/**
 * Answer the width of the item identified by 'itemIndex'.
 */
int getContentWidth(int itemIndex) {
	TreeItem item = getRoot().getVisibleItem(itemIndex);
	int paintStopX = 0;

	if (item != null) {
		paintStopX = item.getPaintStopX();
	}
	return paintStopX;
}
/**
 * Answer the image that is used as a hierarchy indicator 
 * for an expanded hierarchy.
 */
Image getExpandedImage() {
	if (expandedImage == null) {
		expandedImage = new Image(getDisplay(), ExpandedImageData);
	}
	return expandedImage;
}
/**
 * Answer the rectangle enclosing the hierarchy indicator of a tree item.
 * 
 * Note:
 * Assumes that the hierarchy indicators for expanded and 
 * collapsed state are the same size.
 * @return
 *	The rectangle enclosing the hierarchy indicator.
 */
Rectangle getHierarchyIndicatorRect() {
	int itemHeight = getItemHeight();
	Image hierarchyImage;
	Rectangle imageBounds;
	
	if (hierarchyIndicatorRect == null && itemHeight != -1) {
		hierarchyImage = getCollapsedImage();
		if (hierarchyImage != null) {
		 	imageBounds = hierarchyImage.getBounds();
		}
		else {
			imageBounds = new Rectangle(0, 0, 0, 0);
		}
		hierarchyIndicatorRect = new Rectangle(
			0,
			(itemHeight - imageBounds.height) / 2 + (itemHeight - imageBounds.height) % 2,
			imageBounds.width,
			imageBounds.height);
	}
	return hierarchyIndicatorRect;
}
/**
 * Answer the index of 'item' in the receiver.
 */
int getIndex(SelectableItem item) {
	int index = -1;

	if (item != null) {
		index = ((TreeItem) item).getGlobalIndex();
	}
	return index;
}
/**
 * Answer the number of root items.
 */
public int getItemCount() {
	if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	if (!isValidWidget ()) error (SWT.ERROR_WIDGET_DISPOSED);

	return getRoot().getItemCount();
}
/**
 * Answer the height of an item in the receiver.
 * The item height is the greater of the item icon height and 
 * text height of the first item that has text or an image 
 * respectively.
 * Calculate a default item height based on the font height if
 * no item height has been calculated yet.
 */
public int getItemHeight() {
	if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	if (!isValidWidget ()) error (SWT.ERROR_WIDGET_DISPOSED);
	
	return super.getItemHeight();
}
/**
 * Answer the root items of the receiver as an Array.
 */
public TreeItem [] getItems() {
	if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	if (!isValidWidget ()) error (SWT.ERROR_WIDGET_DISPOSED);
	TreeItem childrenArray[] = new TreeItem[getItemCount()];

	getRoot().getChildren().copyInto(childrenArray);
	return childrenArray;	
}
/**
 * Answer the number of sub items of 'item' that do not fit in the 
 * tree client area.
 */
int getOffScreenItemCount(TreeItem item) {
	int itemIndexFromTop = item.getVisibleIndex() - getTopIndex();
	int spaceRemaining = getItemCountWhole()-(itemIndexFromTop+1);
	int expandedItemCount = item.getVisibleItemCount();

	return expandedItemCount - spaceRemaining;	
}
/**
 * Answer the parent item of the receiver. 
 * This is null because the Tree widget does not have a parent item.
 */
public TreeItem getParentItem() {
	if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	if (!isValidWidget ()) error (SWT.ERROR_WIDGET_DISPOSED);

	return null;
}
/**
 * Answer the object that holds the root items of the receiver.
 */
TreeRoots getRoot() {
	return root;
}
/**
 * Answer the selected tree items.
 * @return an Array of DrawnTreeItems containing the selected items.
 *	An empty Array if no items are selected.
 */
public TreeItem [] getSelection() {
	if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	if (!isValidWidget ()) error (SWT.ERROR_WIDGET_DISPOSED);
	Vector selectionVector = getSelectionVector();
	TreeItem[] selectionArray = new TreeItem[selectionVector.size()];

	selectionVector.copyInto(selectionArray);
	sort(selectionArray, 0, selectionArray.length);
	return selectionArray;
}
/**
 * Answer the index of 'item' in the receiver.
 * Answer -1 if the item is not visible.
 * The returned index must refer to a visible item.
 * Note: 
 * 	Visible in this context does not neccessarily mean that the 
 * 	item is displayed on the screen. It only means that the item 
 * 	would be displayed if it is located inside the receiver's 
 * 	client area.
 *	Collapsed items are not visible.
 */
int getVisibleIndex(SelectableItem item) {
	int index = -1;

	if (item != null) {
		index = ((AbstractTreeItem) item).getVisibleIndex();
	}
	return index;
}
/**
 * Answer the SelectableItem located at 'itemIndex' 
 * in the receiver.
 * @param itemIndex - location of the SelectableItem 
 *	object to return
 */
SelectableItem getVisibleItem(int itemIndex) {
	return getRoot().getVisibleItem(itemIndex);
}
/**
 * Answer the number of visible items of the receiver.
 * Note: 
 * 	Visible in this context does not neccessarily mean that the 
 * 	item is displayed on the screen. It only means that the item 
 * 	would be displayed if it is located inside the receiver's 
 * 	client area.
 *	Collapsed items are not visible.
 */
int getVisibleItemCount() {
	return getRoot().getVisibleItemCount();
}
/**
 * Answer the y coordinate at which 'item' is drawn. 
 * @param item - SelectableItem for which the paint position 
 *	should be returned
 * @return the y coordinate at which 'item' is drawn.
 *	Return -1 if 'item' is null or outside the client area
 */
int getVisibleRedrawY(SelectableItem item) {
	int redrawY = getRedrawY(item);
	
	if (redrawY < 0 || redrawY > getClientArea().height) {
		redrawY = -1;
	}
	return redrawY;
}
/**
 * Handle the events the receiver is listening to.
 */
void handleEvents(Event event) {
	switch (event.type) {
		case SWT.Paint:
			paint(event);
			break;
		case SWT.MouseDown:
			mouseDown(event);
			break;
		case SWT.MouseDoubleClick:
			mouseDoubleClick(event);
			break;
		default:
			super.handleEvents(event);
	}	
}
/**
 * Initialize the receiver.
 */
void initialize() {
	resetRoot();					// has to be at very top because super class uses 
									// functionality that relies on the TreeRoots object
	super.initialize();
}
/**
 * Initialize the ImageData used for the expanded/collapsed images.
 */
static void initializeImageData() {
	PaletteData fourBit = new PaletteData(
		new RGB[] {new RGB(0, 0, 0), new RGB (128, 0, 0), new RGB (0, 128, 0), new RGB (128, 128, 0), new RGB (0, 0, 128), new RGB (128, 0, 128), new RGB (0, 128, 128), new RGB (128, 128, 128), new RGB (192, 192, 192), new RGB (255, 0, 0), new RGB (0, 255, 0), new RGB (255, 255, 0), new RGB (0, 0, 255), new RGB (255, 0, 255), new RGB (0, 255, 255), new RGB (255, 255, 255)});
	
	CollapsedImageData = new ImageData(
		9, 9, 4, 										// width, height, depth
		fourBit, 4,
		new byte[] {119, 119, 119, 119, 112, 0, 0, 0, 127, -1, -1, -1, 112, 0, 0, 0, 127, -1, 15, -1, 112, 0, 0, 0, 127, -1, 15, -1, 112, 0, 0, 0, 127, 0, 0, 15, 112, 0, 0, 0, 127, -1, 15, -1, 112, 0, 0, 0, 127, -1, 15, -1, 112, 0, 0, 0, 127, -1, -1, -1, 112, 0, 0, 0, 119, 119, 119, 119, 112, 0, 0, 0});
	CollapsedImageData.transparentPixel = 15;			// use white for transparency
	ExpandedImageData = new ImageData(
		9, 9, 4, 										// width, height, depth
		fourBit, 4,
		new byte[] {119, 119, 119, 119, 112, 0, 0, 0, 127, -1, -1, -1, 112, 0, 0, 0, 127, -1, -1, -1, 112, 0, 0, 0, 127, -1, -1, -1, 112, 0, 0, 0, 127, 0, 0, 15, 112, 0, 0, 0, 127, -1, -1, -1, 112, 0, 0, 0, 127, -1, -1, -1, 112, 0, 0, 0, 127, -1, -1, -1, 112, 0, 0, 0, 119, 119, 119, 119, 112, 0, 0, 0});
	ExpandedImageData.transparentPixel = 15;			// use white for transparency
}
/**
 * Set event listeners for the receiver.
 */
void installListeners() {
	Listener listener = getListener();

	super.installListeners();
	addListener(SWT.Paint, listener);
	addListener(SWT.MouseDown, listener);
	addListener(SWT.MouseDoubleClick, listener);
}
/**
 * Answer whether the receiver is currently expanding a sub tree 
 * with 'item' in it.
 * Used for performance optimizations.
 */
boolean isExpandingItem(SelectableItem item) {
	TreeItem parentItem;
	
	if (expandingItem == null || item == null || (item instanceof TreeItem) == false) {
		return false;
	}
	parentItem = ((TreeItem) item).getParentItem();
	return (parentItem == expandingItem || isExpandingItem(parentItem));
}
/**
 * Answer whether the children of 'collapsingItem' contain 
 * at least one selected item.
 */
boolean isSelectedItemCollapsing(TreeItem collapsingItem) {
	Enumeration selection = getSelectionVector().elements();
	TreeItem item;
	int selectedItemIndex;
	int collapsingItemIndex = collapsingItem.getVisibleIndex();
	int lastCollapsedItemIndex = collapsingItemIndex + collapsingItem.getVisibleItemCount();

	if (collapsingItemIndex == -1) {					// is the collapsing item in a collapsed subtree?
		return false;									// then neither it nor its children are selected
	}
	while (selection.hasMoreElements() == true) {
		item = (TreeItem) selection.nextElement();
		selectedItemIndex = item.getVisibleIndex();
		if ((selectedItemIndex > collapsingItemIndex) &&
			(selectedItemIndex <= lastCollapsedItemIndex)) {
			return true;
		}
	}
	return false;
}
/**
 * Test whether the mouse click specified by 'event' was a 
 * valid selection or expand/collapse click.
 * @return 
 *  One of ActionExpandCollapse, ActionSelect, ActionNone, ActionCheck
 *	specifying the action to be taken on the click.
 */
int itemAction(TreeItem item, int x, int y) {
	int action = ActionNone;
	int itemHeight = getItemHeight();
	int offsetX;
	int offsetY;
	Point offsetPoint;

	if (item != null) {
		offsetX = x - item.getPaintStartX();
		offsetY = y - itemHeight * (y / itemHeight);	
		offsetPoint = new Point(offsetX, offsetY);	
		if ((item.isLeaf() == false) &&
			(getHierarchyIndicatorRect().contains(offsetPoint) == true)) {
			action |= ActionExpandCollapse;
		}
		else
		if (item.isSelectionHit(offsetPoint) == true) {
			action |= ActionSelect;
		}
		else
		if (item.isCheckHit(new Point(x, y)) == true) {
			action |= ActionCheck;
		}
	}
	return action;
}
/**
 * The table item 'changedItem' has changed. Redraw the whole 
 * item in that column. Include the text in the redraw because 
 * an image set to null requires a redraw of the whole item anyway. 
 */
void itemChanged(SelectableItem changedItem, int repaintStartX, int repaintWidth) {
	int oldItemHeight = getItemHeight();	
	Point oldImageExtent = getImageExtent();
	
	if (isExpandingItem(changedItem) == false) {
		super.itemChanged(changedItem, repaintStartX, repaintWidth);
	}
	else {
		calculateItemHeight(changedItem);
	}
	if ((oldItemHeight != getItemHeight()) ||			// only reset items if the item height or
		(oldImageExtent != getImageExtent())) {			// image size has changed. The latter will only change once, 
														// from null to a value-so it's safe to test using !=
		getRoot().reset();								// reset cached data of all items in the receiver
		resetHierarchyIndicatorRect();
		redraw();										// redraw all items if the image extent has changed. Fixes 1FRIHPZ		
	}
	else {
		((AbstractTreeItem) changedItem).reset();		// reset the item that has changed when the tree item 
														// height has not changed (otherwise the item caches old data)
														// Fixes 1FF6B42
	}
	if (repaintWidth != 0) {
		calculateWidestShowingItem();
		claimRightFreeSpace();								// otherwise scroll bar may be reset, but not horizontal offset
															// Fixes 1G4SBJ3
	}
}
/**
 * A key was pressed.
 * Call the appropriate key handler method.
 * @param event - the key event
 */
void keyDown(Event event) {
	super.keyDown(event);
	switch (event.character) {
		case '+':
			doPlus();
			break;
		case '-':
			doMinus();
			break;
		case '*':
			doAsterix();
			break;
	}
}

/**
 * A mouse double clicked occurred over the receiver.
 * Expand/collapse the clicked item. Do nothing if no item was clicked.
 */
void mouseDoubleClick(Event event) {
	int hitItemIndex = event.y / getItemHeight();
	TreeItem hitItem = getRoot().getVisibleItem(hitItemIndex + getTopIndex());
	Event newEvent;
	
	if (hitItem == null) {
		return;
	}
	if (hooks(SWT.DefaultSelection) == true) {
		newEvent = new Event();
		newEvent.item = hitItem;
		notifyListeners(SWT.DefaultSelection, newEvent);
		return;
	}
	if (hitItem.getItemCount() == 0) {		
		return;									// an item was hit but it does not have children
	}
	if (itemAction(hitItem, event.x, event.y) == ActionSelect) {
		if (hitItem.getExpanded() == true) {
			collapse(hitItem, true);
		}
		else {
			expand(hitItem, true);
		}
	}
}
/**
 * The mouse pointer was pressed down on the receiver.
 * Handle the event according to the position of the mouse click.
 */
void mouseDown(Event event) {
	int hitItemIndex;
	TreeItem hitItem;
	SelectableItem selectionItem = getLastSelection();
	int itemAction;

	if (event.button != 1) {		// only react to button one clicks.
		return;
	}
	hitItemIndex = event.y / getItemHeight();
	hitItem = getRoot().getVisibleItem(hitItemIndex + getTopIndex());
	if (hitItem == null) {
		return;
	}
	switch (itemAction = itemAction(hitItem, event.x, event.y)) {
		case ActionExpandCollapse:
			if (hitItem.getExpanded() == true) {
				collapse(hitItem, true);
			}
			else {
				expand(hitItem, true);
			}
			break;
		case ActionSelect:
			doMouseSelect(hitItem, hitItemIndex + getTopIndex(), event.stateMask, event.button);
			break;
		case ActionCheck:
			doCheckItem(hitItem);
			break;
	}
	if (itemAction != ActionSelect && selectionItem == null) {
		selectionItem = getRoot().getVisibleItem(getTopIndex());	// select the top item if no item was selected before
		selectNotify(selectionItem);								
	}
}
/**
 * A paint event has occurred. Display the invalidated items.
 * @param event - expose event specifying the invalidated area.
 */
void paint(Event event) {
	int visibleRange[] = getIndexRange(event.getBounds());
	
	paintItems(event.gc, visibleRange[0], visibleRange[1] + 1); // + 1 to paint the vertical line 
																// connection the last item we really 
																// want to paint with the item after that.
}
/**
 * Paint tree items on 'gc' starting at index 'topPaintIndex' and 
 * stopping at 'bottomPaintIndex'.
 * @param gc - GC to draw tree items on.
 * @param topPaintIndex - index of the first item to draw
 * @param bottomPaintIndex - index of the last item to draw 
 */
void paintItems(GC gc, int topPaintIndex, int bottomPaintIndex) {
	TreeItem visibleItem;
	int itemHeight = getItemHeight();

	for (int i = topPaintIndex; i <= bottomPaintIndex; i++) {
		visibleItem = getRoot().getVisibleItem(i + getTopIndex());
		if (visibleItem != null) {
			visibleItem.paint(gc, i * itemHeight);
		}
	}
}
/**
 * 'item' has been added to or removed from the receiver. 
 * Repaint part of the tree to update the vertical hierarchy 
 * connectors and hierarchy image.
 * @param modifiedItem - the added/removed item 
 * @param modifiedIndex - index of the added/removed item
 */
void redrawAfterModify(SelectableItem modifiedItem, int modifiedIndex) {
	int redrawStartY;
	int redrawStopY;
	int itemChildIndex = ((TreeItem) modifiedItem).getIndex();
	int topIndex = getTopIndex();
	int itemHeight = getItemHeight();
	int redrawItemIndex;
	int itemCount;
	AbstractTreeItem parentItem = ((TreeItem) modifiedItem).getParentItem();
	AbstractTreeItem redrawItem = null;

	if (redrawParentItem(modifiedItem) == false) {
		return;
	}
	if (parentItem == null) {							// a root item is added/removed
		parentItem = getRoot();
	}
	itemCount = parentItem.getItemCount();
	// redraw hierarchy decorations of preceeding item if the last item at a tree 
	// level was added/removed
	// otherwise, if the first item was removed, redraw the parent to update hierarchy icon
	if (itemChildIndex > 0) {							// more than one item left at this tree level
		// added/removed last item at this tree level? have to test >=.
		// when removing last item, item index is outside itemCount 
		if (itemChildIndex >= itemCount - 1) { 
			redrawItem = (AbstractTreeItem) parentItem.getChildren().elementAt(itemChildIndex - 1);
		}
	}
	else 
	if (getVisibleItemCount() > 0 && itemCount < 2) {	// last item at this level removed/first item added?
		redrawItem = parentItem;						// redraw parent item to update hierarchy icon
	}
	if (redrawItem != null) {
		redrawItemIndex = redrawItem.getVisibleIndex();
		if (modifiedIndex == -1) {
			modifiedIndex = redrawItemIndex + 1;
		}
		redrawStartY = (redrawItemIndex - topIndex) * itemHeight;
		redrawStopY = (modifiedIndex - topIndex) * itemHeight;
		redraw(
			0, 
			redrawStartY, 
			redrawItem.getCheckboxXPosition(), 			// only redraw up to and including hierarchy icon to avoid flashing
			redrawStopY - redrawStartY, false);
	}	
	if (modifiedIndex == 0) {											// added/removed first item ?
		redraw(0, 0, getClientArea().width, getItemHeight() * 2, false);// redraw new first two items to 
																		// fix vertical hierarchy line
	}
}

/**
 * Determine if part of the tree hierarchy needs to be redrawn.
 * The hierarchy icon of the parent item of 'item' needs to be redrawn if 
 * 'item' is added as the first child or removed as the last child.
 * Hierarchy lines need to be redrawn if 'item' is the last in a series of 
 * children.
 * @param item - tree item that is added or removed.
 * @return true=tree hierarchy needs to be redrawn. false=no redraw necessary
 */
boolean redrawParentItem(SelectableItem item) {
	TreeItem parentItem = ((TreeItem) item).getParentItem();
	TreeItem parentItem2; 
	boolean redraw = false;

	// determine if only the hierarchy icon needs to be redrawn
	if (parentItem != null) {
		parentItem2 = parentItem.getParentItem();
		if ((parentItem2 == null || parentItem2.getExpanded() == true) && parentItem.getChildren().size() < 2) {
			redraw = true;
		}
	}
	// redraw is only neccessary when the receiver is not currently	
	// expanding 'item' or a parent item or if the parent item is expanded 
	// or if the hierarchy icon of the parent item needs to be redrawn
	if (isExpandingItem(item) == false && parentItem == null || parentItem.getExpanded() == true || redraw == true) {
		redraw = true;
	}
	else {
		redraw = false;
	}
	return redraw;
}

/**
 * Remove all items of the receiver.
 */
public void removeAll() {
	if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	if (!isValidWidget ()) error (SWT.ERROR_WIDGET_DISPOSED);

	setRedraw(false);
	getRoot().dispose();
	resetRoot();
	reset();
	calculateWidestShowingItem();
	calculateVerticalScrollbar();
	setRedraw(true);	
}
/** 
 * Remove 'item' from the receiver. 
 * @param item - tree item that should be removed from the 
 *	receiver-must be a root item.
 */
void removeItem(TreeItem item) {
	getRoot().removeItem(item);
}
/**	 
* Removes the listener.
* <p>
*
* @param listener the listener
*
*/
public void removeSelectionListener(SelectionListener listener) {
	if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	if (!isValidWidget ()) error (SWT.ERROR_WIDGET_DISPOSED);

	if (listener == null) {
		error(SWT.ERROR_NULL_ARGUMENT);
	}	
	removeListener (SWT.Selection, listener);
	removeListener (SWT.DefaultSelection, listener);	
}
/**	 
* Removes the listener.
* <p>
*
* @param listener the listener
*
*/
public void removeTreeListener(TreeListener listener) {
	if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	if (!isValidWidget ()) error (SWT.ERROR_WIDGET_DISPOSED);

	if (listener == null) {
		error(SWT.ERROR_NULL_ARGUMENT);
	}
	removeListener (SWT.Expand, listener);
	removeListener (SWT.Collapse, listener);
}
/**
 * 'item' has been removed from the receiver. 
 * Recalculate the content width.
 */
void removedItem(SelectableItem item) {
	if (isExpandingItem(item) == false) {
		super.removedItem(item);				
	}	
	calculateWidestShowingItem();
	claimRightFreeSpace();
}
/**
 * Notification that 'item' is about to be removed from the tree.
 * Update the item selection if neccessary.
 * @param item - item that is about to be removed from the tree.
 */
void removingItem(SelectableItem item) {
	Vector selection = getSelectionVector();
	TreeItem parentItem = ((TreeItem) item).getParentItem();
	TreeItem newSelectionItem = null;
	boolean isLastSelected = (selection.size() == 1) && (selection.elementAt(0) == item);
	int itemIndex = getVisibleIndex(item);
	
	if (isLastSelected == true) {
		// try selecting the following item
		newSelectionItem = (TreeItem) getVisibleItem(itemIndex + 1);
		if (newSelectionItem == null || newSelectionItem.getParentItem() != parentItem) {
			// select parent item if there is no item following the removed  
			// one on the same tree level
			newSelectionItem = parentItem;
		}
		if (newSelectionItem != null) {
			selectNotify(newSelectionItem);
		}
	}
	super.removingItem(item);
	if (isExpandingItem(item) == false) {
		// redraw plus/minus image, hierarchy lines,
		// redrawing here assumes that no update happens between now and 
		// after the item has actually been removed. Otherwise this call 
		// would need to be in removedItem and we would need to store the
		// "itemIndex" here to redraw correctly.
		redrawAfterModify(item, itemIndex);
	}	
}
/**
 * Reset the rectangle enclosing the hierarchy indicator to null.
 * Forces a recalculation next time getHierarchyIndicatorRect is called.
 */
void resetHierarchyIndicatorRect() {
	hierarchyIndicatorRect = null;
}
/**
 * Reset state that is dependent on or calculated from the items
 * of the receiver.
 */
void resetItemData() {
	setContentWidth(0);
	resetHierarchyIndicatorRect();	
	super.resetItemData();	
}
/**
 * Reset the object holding the root items of the receiver.
 */
void resetRoot() {
	root = new TreeRoots(this);
}
/**
 * The receiver has been resized. Recalculate the content width.
 */
void resize(Event event) {
	int oldItemCount = getVerticalBar().getPageIncrement();

	super.resize(event);
	if (getItemCountWhole() > oldItemCount) {		// window resized higher?
		calculateWidestShowingItem();				// recalculate widest item since a longer item may be visible now
	}
}
/**
 * Display as many expanded tree items as possible.
 * Scroll the last expanded child to the bottom if all expanded 
 * children can be displayed.
 * Otherwise scroll the expanded item to the top.
 * @param item - the tree item that was expanded
 */
void scrollExpandedItemsIntoView(TreeItem item) {
	int itemCountOffScreen = getOffScreenItemCount(item);
	int newTopIndex = getTopIndex() + itemCountOffScreen;

	if (itemCountOffScreen > 0) {
		newTopIndex = Math.min(item.getVisibleIndex(), newTopIndex);	// make sure the expanded item is never scrolled out of view
		setTopIndex(newTopIndex, true);								
	}
}
/**
 * Scroll the items following the children of 'collapsedItem'
 * below 'collapsedItem' to cover the collapsed children.
 * @param collapsedItem - item that has been collapsed
 */
void scrollForCollapse(TreeItem collapsedItem) {
	Rectangle clientArea = getClientArea();	
	int topIndex = getTopIndex();
	int itemCount = collapsedItem.getVisibleItemCount();
	int scrollYPositions[] = calculateChildrenYPos(collapsedItem);

	if (scrollYPositions[0] == -1 && scrollYPositions[1] == -1) {
		return;
	}
	if (topIndex + getItemCountWhole() == getVisibleItemCount() && itemCount < topIndex) {
		// scroll from top if last item is at bottom and will stay at 
		// bottom after collapse. Avoids flash caused by too much bit 
		// blitting (which force update and thus premature redraw)
		int height = scrollYPositions[1] - scrollYPositions[0];
		scroll(
			0, 0,					// destination x, y
			0, -height,				// source x, y		
			clientArea.width, scrollYPositions[0]+height, true);
		setTopIndexNoScroll(topIndex - itemCount, true);
	}	
	else {
		scroll(
			0, scrollYPositions[0],				// destination x, y
			0, scrollYPositions[1],				// source x, y		
			clientArea.width, clientArea.height - scrollYPositions[0], true);
	}
}
/**
 * Scroll the items following 'expandedItem' down to make 
 * space for the children of 'expandedItem'.
 * @param expandedItem - item that has been expanded.
 */
void scrollForExpand(TreeItem expandedItem) {
	int scrollYPositions[];
	Rectangle clientArea = getClientArea();

	expandedItem.internalSetExpanded(true);		
	scrollYPositions = calculateChildrenYPos(expandedItem);	
	expandedItem.internalSetExpanded(false);	
	if (scrollYPositions[0] == -1 && scrollYPositions[1] == -1) {
		return;
	}	
	scroll(
		0, scrollYPositions[1],				// destination x, y
		0, scrollYPositions[0],				// source x, y
		clientArea.width, clientArea.height, true);
}
/**
 * Scroll horizontally by 'numPixel' pixel.
 * @param numPixel - the number of pixel to scroll
 *	< 0 = columns are going to be moved left.
 *	> 0 = columns are going to be moved right.
 */
void scrollHorizontal(int numPixel) {
	Rectangle clientArea = getClientArea();

	scroll(
		numPixel, 0, 								// destination x, y
		0, 0, 										// source x, y
		clientArea.width, clientArea.height, true);
}
/**
 * Scroll vertically by 'scrollIndexCount' items.
 * @param scrollIndexCount - the number of items to scroll.
 *	scrollIndexCount > 0 = scroll up. scrollIndexCount < 0 = scroll down
 */
void scrollVertical(int scrollIndexCount) {
	Rectangle clientArea = getClientArea();

	scroll(
		0, 0, 										// destination x, y
		0, scrollIndexCount * getItemHeight(),		// source x, y
		clientArea.width, clientArea.height, true);
}
/**
 * Select all items of the receiver if it is in multiple 
 * selection mode.
 * A SWT.Selection event will not be sent.
 * Do nothing if the receiver is in single selection mode.
 */
public void selectAll() {
	if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	if (!isValidWidget ()) error (SWT.ERROR_WIDGET_DISPOSED);
	Vector selection = getSelectionVector();

	if (isMultiSelect() == true) {
		selection = getRoot().selectAll(selection);
		setSelectionVector(selection);
	}
}
/**
 * Set the item that is currently being expanded to 'item'.
 * Used for performance optimizations.
 */
void setExpandingItem(TreeItem item) {
	expandingItem = item;
}
/**
 * The font is changing. Reset and recalculate the item 
 * height using all items of the receiver.
 */
public void setFont(Font font) {
	if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	if (!isValidWidget ()) error (SWT.ERROR_WIDGET_DISPOSED);
	Stack children = new Stack();						// traverse the tree depth first
	Enumeration elements;
	AbstractTreeItem item;

	if (font != null && font.equals(getFont()) == true) {
		return;
	}
	setRedraw(false);									// disable redraw because itemChanged() triggers undesired redraw
	resetItemData();	
	super.setFont(font);

	// Call itemChanged for all tree items
	elements = getRoot().getChildren().elements();
	while (elements.hasMoreElements() == true) {
		children.push(elements.nextElement());
	}			
	while (children.empty() == false) {
		item = (AbstractTreeItem) children.pop();
		itemChanged(item, 0, getClientArea().width);
		elements = item.getChildren().elements();
		while (elements.hasMoreElements() == true) {
			children.push(elements.nextElement());
		}			
	}
	setRedraw(true);									// re-enable redraw
}
/**
 * Display a mark indicating the point at which an item will be inserted.
 * The drop insert item has a visual hint to show where a dragged item 
 * will be inserted when dropped on the tree.
 * <p>
 * @param item the insert item.  Null will clear the insertion mark.
 * @param after true places the insert mark above 'item'. false places 
 *	the insert mark below 'item'.
 */
public void setInsertMark(TreeItem item, boolean before){
	if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	if (!isValidWidget ()) error (SWT.ERROR_WIDGET_DISPOSED);

	motif_setInsertMark(item, !before);
}
/**
 * Select the items stored in 'selectionItems'. 
 * A SWT.Selection event is not going to be sent.
 * @param selectionItems - Array containing the items that should 
 *	be selected
 */
public void setSelection(TreeItem selectionItems[]) {
	if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	if (!isValidWidget ()) error (SWT.ERROR_WIDGET_DISPOSED);
	
	if (selectionItems == null)  {
		error(SWT.ERROR_NULL_ARGUMENT);
	}	
	setSelectableSelection(selectionItems);
}
/**
 * Set the index of the first visible item in the tree client area 
 * to 'index'.
 * Scroll the new top item to the top of the tree.
 * @param index - 0-based index of the first visible item in the 
 *	tree's client area.
 * @param adjustScrollbar - 
 *	true = the vertical scroll bar is set to reflect the new top index.
 *	false = the vertical scroll bar position is not modified.
 */
void setTopIndex(int index, boolean adjustScrollbar) {
	int indexDiff = index-getTopIndex();

	super.setTopIndex(index, adjustScrollbar);
	calculateWidestScrolledItem(indexDiff);
}
/**
 * Make 'item' visible by expanding its parent items and scrolling 
 * it into the receiver's client area if necessary.
 * An SWT.Expand event is going to be sent for every parent item 
 * that is expanded to make 'item' visible.
 * @param item - the item that should be made visible to the
 *	user.
 */
public void showItem(TreeItem item) {
	if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	if (!isValidWidget ()) error (SWT.ERROR_WIDGET_DISPOSED);

	if (item == null)  {
		error(SWT.ERROR_NULL_ARGUMENT);
	}	
	showSelectableItem(item);
}
/**
 * Make 'item' visible by expanding its parent items and scrolling 
 * it into the receiver's client area if necessary.
 * An SWT.Expand event is going to be sent for every parent item 
 * that is expanded to make 'item' visible.
 * @param item - the item that should be made visible to the
 *	user.
 */
void showSelectableItem(SelectableItem item) {
	if (item.getSelectableParent() != this) {
		return;
	}
	if (((TreeItem) item).isVisible() == false) {
		((TreeItem) item).makeVisible();
	}
	super.showSelectableItem(item);
}
/**
 * Return the item at the specified location in the widget.
 * Return null if there is no item at the specified location
 * or if the location is outside the widget client area.
 */
public TreeItem getItem(Point point) {
	int itemHeight;
	int hitItemIndex;
	TreeItem hitItem;

	if (getClientArea().contains(point) == false) {
		return null;
	}	
	itemHeight = getItemHeight();
	hitItemIndex = point.y / itemHeight;
	hitItem = getRoot().getVisibleItem(hitItemIndex + getTopIndex());
	if (hitItem != null) {
		point.x -= hitItem.getPaintStartX();
		point.y -= itemHeight * hitItemIndex;			
		if (hitItem.isSelectionHit(point) == false) {
			hitItem = null;
		}
	}
	return hitItem;
}
/**
 * Answer the number of selected items in the receiver.
 */
public int getSelectionCount() {
	if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	if (!isValidWidget ()) error (SWT.ERROR_WIDGET_DISPOSED);

	return super.getSelectionCount();
}
/**
 * Show the selection. If there is no selection or the 
 * selection is already visible, this method does nothing. 
 * If the selection is not visible, the top index of the 
 * widget is changed such that the selection becomes visible.
 */
public void showSelection() {
	if (!isValidThread ()) error (SWT.ERROR_THREAD_INVALID_ACCESS);
	if (!isValidWidget ()) error (SWT.ERROR_WIDGET_DISPOSED);
	
	super.showSelection();
}

}
