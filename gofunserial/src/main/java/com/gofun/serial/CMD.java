package com.gofun.serial;


public class CMD {
  private static final String HEAD = "5B";       //帧头标志都是0x5B。
  private static final String VERSION_CRYPTIC = "30";     //协议版本号（4bit），目前值为0x3,0表示采取“数据不加密+校验和”方式。1表示采用“数据加密+校验和”方式。
  private static final String CR_BIT = "01";     //1表示命令帧，0表示应答帧。
  private static final String TF_BIT = "00";     //1表示透明的非结构化数据，0表示正常的数据帧。
  private static String cmd_count = "1";
  private static final String CMD_LENGTH = "0002";


  public static byte[] sendCMD(CMD_TYPE type){
    String cmdStr = "";
    switch (type){
      case CMD_REGISTER:
        cmdStr = CMD_TYPE.CMD_REGISTER.getName();
        break;
      case CMD_CAR_DETAIL:
        cmdStr = CMD_TYPE.CMD_CAR_DETAIL.getName();
        break;
    }

    StringBuilder preStr = new StringBuilder(
            HEAD +
                    VERSION_CRYPTIC+
                    CR_BIT+
                    TF_BIT +
                    getCMDCount() +
                    CMD_LENGTH +
                    cmdStr);

    byte[] preBytes = ByteUtil.hexStrToByteArray(preStr.toString());
    byte[] crcBytes = new byte[2];
    CRC16Util.get_crc16(preBytes,preBytes.length,crcBytes);
    cmd_count = Integer.toHexString((Integer.parseInt(cmd_count,16)+1));
    return ByteUtil.concat(preBytes,crcBytes);

  }

  private static String getCMDCount(){
    int count = Integer.parseInt(cmd_count,16);
    if (count >= 16 * 16 || count < 1) {
      count = 1;
    }

    cmd_count = Integer.toHexString(count);

    if (cmd_count.length()<2) {
      cmd_count = "0"+cmd_count;
    }

    return cmd_count;
  }

  enum CMD_TYPE{
    CMD_REGISTER("1100"),CMD_CAR_DETAIL("1101");
    private String name;

    private CMD_TYPE(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }




}
