package scaledmarkets.dabl.gen;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.Config;
import scaledmarkets.dabl.analysis.*;

import java.util.Set;
import java.util.TreeSet;

public class Artifact {
	
	private AOartifactSet artifactSet;
	private Set<Task> consumers = new TreeSet<Task>();
	private Set<Task> producers = new TreeSet<Task>();
	
	Artifact(AOartifactSet artifactSet) {
		this.artifactSet = artifactSet;
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
