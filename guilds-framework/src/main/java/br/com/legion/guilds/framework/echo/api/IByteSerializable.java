package br.com.legion.guilds.framework.echo.api;

public interface IByteSerializable {

    void write(EchoBufferOutput buffer);

    void read(EchoBufferInput buffer);

}
