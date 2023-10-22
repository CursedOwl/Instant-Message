package com.im.server.message;


/**
 * @ClassName 协议头各字段均用一个byte表示
 */
public class IMProtocol<T> {
    private byte magic;

    private byte version;

    private byte serializeAlgorithm;

    private byte encrypt;

    private byte command;

    private byte status;

    private byte dataType;

    private Integer length;

    private Class<?> clazz;

    private T body;

    public IMProtocol() {
    }

    public IMProtocol(byte magic, byte version, byte serializeAlgorithm, byte command, byte status, Integer length, T body) {
        this.magic = magic;
        this.version = version;
        this.serializeAlgorithm = serializeAlgorithm;
        this.command = command;
        this.status = status;
        this.length = length;
        this.body = body;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public byte getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(byte encrypt) {
        this.encrypt = encrypt;
    }

    public byte getDataType() {
        return dataType;
    }

    public void setDataType(byte dataType) {
        this.dataType = dataType;
    }

    public byte getMagic() {
        return magic;
    }

    public void setMagic(byte magic) {
        this.magic = magic;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public byte getSerializeAlgorithm() {
        return serializeAlgorithm;
    }

    public void setSerializeAlgorithm(byte serializeAlgorithm) {
        this.serializeAlgorithm = serializeAlgorithm;
    }

    public byte getCommand() {
        return command;
    }

    public void setCommand(byte command) {
        this.command = command;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "IMProtocol{" +
                "magic=" + magic +
                ", version=" + version +
                ", serializeAlgorithm=" + serializeAlgorithm +
                ", encrypt=" + encrypt +
                ", command=" + command +
                ", status=" + status +
                ", dataType=" + dataType +
                ", length=" + length +
                ", clazz=" + clazz +
                ", body=" + body +
                '}';
    }
}
