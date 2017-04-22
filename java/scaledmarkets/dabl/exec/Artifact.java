package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.Config;
import scaledmarkets.dabl.analysis.*;

import java.util.Set;
import java.util.HashSet;

public class Artifact {
	
	private POartifactSet artifactSet;
	private Set<Task> consumers = new HashSet<Task>();
	private Set<Task> producers = new HashSet<Task>();
	
	Artifact(POartifactSet artifactSet) {
		this.artifactSet = artifactSet;
	}
	
	public POartifactSet getArtifactSet() {
		return artifactSet;
	}
	
	void addProducer(Task t) {
		producers.add(t);
	}
	
	void addConsumer(Task t) {
		consumers.add(t);
	}
	
	Set<Task> getConsumers() {
		return consumers;
	}
	
	Set<Task> getProducers() {
		return producers;
	}
}
