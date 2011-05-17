package it.unibo.cs.v2.servlets;

import it.unibo.cs.v2.shared.MachineInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("sharemachine")
public interface ShareMachine extends RemoteService {
	public boolean shareMachine(MachineInfo machine, String user) throws Exception;
}
