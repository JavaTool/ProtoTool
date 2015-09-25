package tool.proto;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;

public class MessageIdScaner {
	
	public static final String HEAD = "MI_";
	
	public static final String REQUEST = "REQUEST_";
	
	public static final String RESPONSE = "RESPONSE_";
	
	private static final String HEAD_REQUEST = HEAD + REQUEST;
	
	private static final String HEAD_RESPONSE = HEAD + RESPONSE;
	
	public Map<String, ProtoMessage> scan(File file) throws Exception {
		LineNumberReader reader = new LineNumberReader(new FileReader(file));
		Map<String, ProtoMessage> messages = new HashMap<String, ProtoMessage>();
		try {
			String line;
			String protoName = null;
			while ((line = reader.readLine()) != null) {
				int start = line.indexOf(HEAD);
				int end = line.indexOf(';');
				if (start > -1 && end > -1) {
					line = line.substring(start, end).trim();
					String[] infos = line.split("=");
					ProtoMessage protoMessage = new ProtoMessage();
					
					if (infos[0].startsWith(HEAD_REQUEST)) {
						protoMessage.setHead(REQUEST);
					} else if (infos[0].startsWith(HEAD_RESPONSE)) {
						protoMessage.setHead(RESPONSE);
					} else {
						throw new Exception("Unknow line : " + line);
					}
					
					protoMessage.setMessageIdName(infos[0]);
					protoMessage.setMessageIdValue(Integer.parseInt(infos[1]));
					protoMessage.setProtoName(protoName);
					
					messages.put(infos[0], protoMessage);
				} else if (line.contains("@proto")) {
					protoName = line.contains("=") ? line.split("=")[1] : null;
				}
			}
		} finally {
			reader.close();
		}
		return messages;
	}

}
