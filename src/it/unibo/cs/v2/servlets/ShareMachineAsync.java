package it.unibo.cs.v2.servlets;

import it.unibo.cs.v2.shared.MachineInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ShareMachineAsync {
	void shareMachine(MachineInfo machine, String user,	AsyncCallback<Boolean> callback);
}
