package core;

import java.util.ArrayList;

// This file is no longer used.
 /**
  * We need to remember the original deviceNum variable of the Device and the Device itself.
  * Each deviceAssignment Object contains a List of driverAssignments which remembers
  * the original driverNum resp. driver.
  *
  * @see driverAssignment
  * @deprecated driverNum is no longer used now.
  */
class deviceAssignment
{
  protected int       deviceNum;
  protected Device    device;
  protected ArrayList driverAssignmentList = new ArrayList();

  deviceAssignment(int deviceNum, Device device)
  {
    this.deviceNum = deviceNum;
    this.device    = device;
  }

  void add(int driverNum, Driver driver)
  {
    this.driverAssignmentList.add(new driverAssignment(driverNum, driver));
  }

  public int getDeviceNum() {
    return deviceNum;
  }
  public Device getDevice() {
    return device;
  }
  public ArrayList getDriverAssignmentList() {
    return driverAssignmentList;
  }

  public String toString() {
    return device.toString();
  }
}


/**
 * We need to remember the original driverNum variable of the Driver.
 * This is deprecated deviceNum is no longer used now.
 *
 * @see deviceAssignment
 */
class driverAssignment
{
  protected int    driverNum;
  protected Driver driver;

  driverAssignment(int driverNum, Driver driver)
  {
    this.driverNum = driverNum;
    this.driver    = driver;
  }

  public Driver getDriver() {
    return driver;
  }

  public int getDriverNum() {
    return driverNum;
  }

  public String toString() {
    return driver.toString();
  }
}
