package com.founder.amuc.msg;

import org.smslib.IOutboundMessageNotification;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.Message.MessageEncodings;
import org.smslib.modem.SerialModemGateway;

public class SendMessage
{
  private Service srv;//短信服务
  private OutboundMessage msg;//群发短信实体
  private String message;//群发短信的内容
  public String getMessage() {
    return message;
  }
  public void setMessage(String message){
    this.message = message;
  }
  /**
   * 服务初始化，启动短信服务
   * @throws Exception
   */
  public void serviceStart()throws Exception{
    
    srv = new Service();
    //OutboundNotification outboundNotification = new OutboundNotification();
    
    //115200是波特率，一般为9600。可以通过超级终端测试出来
    SerialModemGateway gateway = new SerialModemGateway("modem.com1", "COM1", 9600, "wavecom", "17254");
    gateway.setInbound(true);//设置true，表示该网关可以接收短信,根据需求修改
    gateway.setOutbound(true);//设置true，表示该网关可以发送短信,根据需求修改
    gateway.setSimPin("0000");//sim卡锁，一般默认为0000或1234
    //gateway.setOutboundNotification(outboundNotification);
    srv.addGateway(gateway);//将网关添加到短信猫服务中
    srv.startService();//启动服务，进入短信发送就绪状态
    
    //打印设备信息
    System.out.println("Modem Information:");
    System.out.println("  Manufacturer: " + gateway.getManufacturer());//制造商
    System.out.println("  Model: " + gateway.getModel());
    System.out.println("  Serial No: " + gateway.getSerialNo());
    System.out.println("  SIM IMSI: " + gateway.getImsi());
    System.out.println("  Signal Level: " + gateway.getSignalLevel() + "%");
    System.out.println("  Battery Level: " + gateway.getBatteryLevel() + "%");
    System.out.println();
    
  }
  /**
   * 发送短信
   * @param mobile 接收方手机号
   * @return
   * @throws Exception
   */
  public int sendMessage(String mobile) throws Exception{
    
    msg = new OutboundMessage(mobile, message);//接收方(手机号码)+内容
    msg.setEncoding(MessageEncodings.ENCUCS2);//这句话是发中文短信必须的
    boolean status = srv.sendMessage(msg);//执行发送短信
    //发送短信后的状态，1代表成功，3代表失败。
    if(status==true){
      return 1;
    }else
      return 3;
    
  }
  /**
   * 关闭短信服务
   * @throws Exception
   */
  public void serviceStop()  throws Exception{
    srv.stopService();
  }
  /**
   * 短信发送成功后，调用该接口。并将发送短信的网关和短信内容对象传给process接口
   */
  public class OutboundNotification implements IOutboundMessageNotification
  {
    public void process(String gatewayId, OutboundMessage msg)
    {
      System.out.println("Outbound handler called from Gateway: " + gatewayId);
      System.out.println(msg);
    }
  }
}
