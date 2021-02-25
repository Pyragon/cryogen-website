/*
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/


package com.cryo.utils.cs2;

public class TextUtils {

	public static String quote(String str) {
		StringBuffer result = new StringBuffer("\"");
		for (int i = 0; i < str.length(); i++) {
			char c;
			switch (c = str.charAt(i)) {
				case '\0' :
					result.append("\\0");
					break;
				case '\t' :
					result.append("\\t");
					break;
				case '\n' :
					result.append("\\n");
					break;
				case '\r' :
					result.append("\\r");
					break;
				case '\\' :
					result.append("\\\\");
					break;
				case '\"' :
					result.append("\\\"");
					break;
				default :
					if (c < 32) {
						String oct = Integer.toOctalString(c);
						result.append("\\000".substring(0, 4 - oct.length())).append(oct);
					} else if (c >= 32 && c < 127)
						result.append(str.charAt(i));
					else {
						String hex = Integer.toHexString(c);
						result.append("\\u0000".substring(0, 6 - hex.length())).append(hex);
					}
			}
		}
		return result.append("\"").toString();
	}

	public static String quote(char c) {
		switch (c) {
			case '\0' :
				return "\'\\0\'";
			case '\t' :
				return "\'\\t\'";
			case '\n' :
				return "\'\\n\'";
			case '\r' :
				return "\'\\r\'";
			case '\\' :
				return "\'\\\\\'";
			case '\"' :
				return "\'\\\"\'";
			case '\'' :
				return "\'\\\'\'";
		}
		if (c < 32) {
			String oct = Integer.toOctalString(c);
			return "\'\\000".substring(0, 5 - oct.length()) + oct + "\'";
		}
		if (c >= 32 && c < 127)
			return "\'" + c + "\'";
		else {
			String hex = Integer.toHexString(c);
			return "\'\\u0000".substring(0, 7 - hex.length()) + hex + "\'";
		}
	}
}
