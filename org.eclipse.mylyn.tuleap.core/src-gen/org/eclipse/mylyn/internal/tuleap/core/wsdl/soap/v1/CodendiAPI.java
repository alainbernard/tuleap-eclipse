/**
 * CodendiAPI.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package org.eclipse.mylyn.internal.tuleap.core.wsdl.soap.v1;

@SuppressWarnings("all")
public interface CodendiAPI extends javax.xml.rpc.Service {
    public java.lang.String getCodendiAPIPortAddress();

    public org.eclipse.mylyn.internal.tuleap.core.wsdl.soap.v1.CodendiAPIPortType getCodendiAPIPort() throws javax.xml.rpc.ServiceException;

    public org.eclipse.mylyn.internal.tuleap.core.wsdl.soap.v1.CodendiAPIPortType getCodendiAPIPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
