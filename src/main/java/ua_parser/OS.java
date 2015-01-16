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

import java.util.Map;

/**
 * Operating System parsed data class
 *
 * @author Steve Jiang (@sjiang) <gh at iamsteve com>
 */
public class OS {
  public final String family, major, minor, patch, patchMinor;

  public OS(String family, String major, String minor, String patch, String patchMinor) {
    this.family = family;
    this.major = major;
    this.minor = minor;
    this.patch = patch;
    this.patchMinor = patchMinor;
  }

  public static OS fromMap(Map<String, String> m) {
    return new OS(m.get("family"), m.get("major"), m.get("minor"), m.get("patch"), m.get("patch_minor"));
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof OS)) return false;

    OS o = (OS) other;
    return ((this.family != null && this.family.equals(o.family)) || this.family == o.family) &&
           ((this.major != null && this.major.equals(o.major)) || this.major == o.major) &&
           ((this.minor != null && this.minor.equals(o.minor)) || this.minor == o.minor) &&
           ((this.patch != null && this.patch.equals(o.patch)) || this.patch == o.patch) &&
           ((this.patchMinor != null && this.patchMinor.equals(o.patchMinor)) || this.patchMinor == o.patchMinor);
  }

  @Override
  public int hashCode() {
    int result = family != null ? family.hashCode() : 0;
    result = 31 * result + (major != null ? major.hashCode() : 0);
    result = 31 * result + (minor != null ? minor.hashCode() : 0);
    result = 31 * result + (patch != null ? patch.hashCode() : 0);
    result = 31 * result + (patchMinor != null ? patchMinor.hashCode() : 0);
    return result;
  }

    @Override
  public String toString() {
    return String.format("{\"family\": %s, \"major\": %s, \"minor\": %s, \"patch\": %s, \"patch_minor\": %s}",
                         family == null ? Constants.EMPTY_STRING : '"' + family + '"',
                         major == null ? Constants.EMPTY_STRING : '"' + major + '"',
                         minor == null ? Constants.EMPTY_STRING : '"' + minor + '"',
                         patch == null ? Constants.EMPTY_STRING : '"' + patch + '"',
                         patchMinor == null ? Constants.EMPTY_STRING : '"' + patchMinor + '"');
  }
}