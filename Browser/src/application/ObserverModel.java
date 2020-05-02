package application;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class ObserverModel {
	protected PropertyChangeSupport model;
	
	public ObserverModel() {
		model = new PropertyChangeSupport(this);
	}
	
	public void addListener(PropertyChangeListener listener) {
		model.addPropertyChangeListener(listener);
	}
	
	public void removeListener(PropertyChangeListener listener) {
		model.removePropertyChangeListener(listener);
	}
	
	public void fireEvent(String name, String value) {
		model.firePropertyChange(name, null, value);
	}
	
}