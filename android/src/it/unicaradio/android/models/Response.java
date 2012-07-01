package it.unicaradio.android.models;

import it.unicaradio.android.enums.Error;

public class Response<T>
{
	private T result;

	private Error errorCode;

	public Response()
	{
		errorCode = Error.OK;
	}

	public Response(T result)
	{
		this.result = result;
		this.errorCode = Error.OK;
	}

	public Response(Error error)
	{
		this.result = null;
		this.errorCode = error;
	}

	public boolean containsError()
	{
		return errorCode != Error.OK;
	}

	public T getResult()
	{
		return result;
	}

	public void setResult(T result)
	{
		this.result = result;
	}

	public Error getErrorCode()
	{
		return errorCode;
	}

	public void setErrorCode(Error errorCode)
	{
		this.errorCode = errorCode;
	}
}
