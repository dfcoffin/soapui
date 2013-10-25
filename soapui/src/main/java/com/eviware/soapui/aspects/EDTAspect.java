package com.eviware.soapui.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.awt.*;

@Aspect
public class EDTAspect
{

	@Pointcut("call (* javax.swing..*+.*(..)) || " +
			"call (javax.swing..*+.new(..))")
	public void swingMethods() {}

	@Pointcut("call (* javax.swing..*+.add*Listener(..)) || "
			+ "call (* javax.swing..*+.remove*Listener(..)) || "
			+ "call (* javax.swing..*+.getListeners(..)) || "
			+ "call (* javax.swing..*+.revalidate()) || "
			+ "call (* javax.swing..*+.invalidate()) || "
			+ "call (* javax.swing..*+.repaint()) || "
			+ "call (* javax.swing..*+.isEnabled()) || "
			+ "target (javax.swing.SwingWorker+) || "
			+ "call (* javax.swing.SwingUtilities+.invoke*(..)) || "
			+ "call (* javax.swing.SwingUtilities+.isEventDispatchThread()) || "
			+ "call (void javax.swing.JComponent+.setText(java.lang.String))")
	public void safeMethods() {}

	@Before("swingMethods() && !safeMethods() && !within(EDTCheck)")
	public void checkCallingThread(JoinPoint.StaticPart thisJoinPointStatic) {
		if(!EventQueue.isDispatchThread()) {
			System.err.println("Swing EDT violation: " + thisJoinPointStatic.getSignature()
					+ " (" + thisJoinPointStatic.getSourceLocation() + ")");
			Thread.dumpStack();
		}
	}

}
