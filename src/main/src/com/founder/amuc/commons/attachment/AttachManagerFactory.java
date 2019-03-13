package com.founder.amuc.commons.attachment;

public class AttachManagerFactory
{
	private static AttachManager dam = new AttachManagerImpl();

	public static AttachManager getInstance()
	{
		return dam;
	}
}
