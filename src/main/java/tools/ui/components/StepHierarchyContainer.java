package tools.ui.components;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.data.Collapsible;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

import tools.beans.ErrorCategory;

public class StepHierarchyContainer implements Collapsible {

	int build;
	Set<ErrorCategory> expandedCategory = new HashSet<>();

	public StepHierarchyContainer(int build) {
		this.build = build;
	}

	@Override
	public Collection<?> getChildren(Object itemId) {
		if (itemId instanceof ErrorCategory) {
			
		}
		
		return null;
	}

	@Override
	public Object getParent(Object itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<?> rootItemIds() {
		return Arrays.asList(ErrorCategory.values());
	}

	@Override
	public boolean setParent(Object itemId, Object newParentId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean areChildrenAllowed(Object itemId) {
		return false;
	}

	@Override
	public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRoot(Object itemId) {
		if (itemId instanceof ErrorCategory)
			return true;
		return false;
	}

	@Override
	public boolean hasChildren(Object itemId) {
		if(itemId instanceof ErrorCategory)
			return true;
		return false;
	}

	@Override
	public boolean removeItem(Object itemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Item getItem(Object itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<?> getContainerPropertyIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<?> getItemIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property getContainerProperty(Object itemId, Object propertyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> getType(Object propertyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean containsId(Object itemId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object addItem() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object nextItemId(Object itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object prevItemId(Object itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object firstItemId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object lastItemId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFirstId(Object itemId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLastId(Object itemId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object addItemAfter(Object previousItemId) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item addItemAfter(Object previousItemId, Object newItemId) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCollapsed(Object itemId, boolean collapsed) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCollapsed(Object itemId) {
		// TODO Auto-generated method stub
		return false;
	}

}
