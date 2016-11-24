/**
 * Copyright 2012 Twitter, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ua_parser;

/**
 * Collection of parsed data for a given user agent string consisting of UserAgent, OS, Device
 *
 * @author Steve Jiang (@sjiang) <gh at iamsteve com>
 */
public class Client {
  public final UserAgent userAgent;
  public final OS os;
  public final Device device;
  public final Platform platform;
  public final Manufacture manufacture;

  public Client(UserAgent userAgent, OS os, Device device, Platform platform, Manufacture manufacture) {
    this.userAgent = userAgent;
    this.os = os;
    this.device = device;
    this.platform = platform;
    this.manufacture = manufacture;
  }

  /**
   * Calculates a pretty string of the browser version from the user agent
   *
   * @return           A string with format X.Y.Z
   */
  public String calculateBrowserVersion() {
    final String browserVersion;

    // Build the browser version from all it's parts
    if (this.userAgent.major != null) {
      browserVersion = this.userAgent.major;

      if (this.userAgent.minor != null) {
        browserVersion.concat("."+this.userAgent.minor);

        if (this.userAgent.patch != null) {
          browserVersion.concat("."+this.userAgent.patch);
        }
      }
    } else {
      browserVersion = Constants.UNDEFINED;
    }
    return browserVersion;
  }

  /**
   * Calculates the name of the operating system in a specific format:
   *
   * eg: iOS 8, iOS 7, Android 2.0
   * @return iOS 8, iOS 7, Android 2.0
   */
  public String calculateOSName() {
    final String osName = this.os.family;

    if(this.os.major != null) {
      osName.concat(" " + this.os.major);

      if (this.os.minor != null) {
        osName.concat("." + this.os.minor);
      }
    }

    return osName;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Client)) return false;

    Client o = (Client) other;
    return ((this.userAgent != null && this.userAgent.equals(o.userAgent)) || this.userAgent == o.userAgent) &&
           ((this.os != null && this.os.equals(o.os)) || this.os == o.os) &&
           ((this.device != null && this.device.equals(o.device)) || this.device == o.device);
  }

  @Override
  public int hashCode() {
    int h = userAgent == null ? 0 : userAgent.hashCode();
    h += os == null ? 0 : os.hashCode();
    h += device == null ? 0 : device.hashCode();
    return h;
  }

  @Override
  public String toString() {
    return String.format("{\"user_agent\": %s, \"os\": %s, \"device\": %s}",
        userAgent, os, device);
  }

}