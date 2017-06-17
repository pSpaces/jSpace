/**
 * 
 * jSpace: a Java Framework for Programming Concurrent and Distributed Applications with Spaces
 * 
 * http://pspace.github.io/jSpace/	
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Alberto Lluch Lafuente
 *      Michele Loreti
 *      Francesco Terrosi
 */
package org.jspace.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * @author loreti
 *
 */
public interface jSpaceMarshaller {
	
	public byte[] toByte(Object o);
	
	public <T> T fromByte(Class<T> clazz, byte[] data);

	public <T> T read(Class<T> clazz, BufferedReader reader) throws IOException;

	public void write(Object o, PrintWriter writer);

}
