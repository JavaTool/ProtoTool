package tool.proto;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;

public class MessageIdScaner {
	
	private static final String HEAD = "MI_";
	
	private static final String REQUEST = HEAD + "REQUEST_";
	
	private static final String RESPONSE = HEAD + "RESPONSE_";
	
	public Map<String, ProtoMessage> scan(File file) throws Exception {
		LineNumberReader reader = new LineNumberReader(new FileReader(file));
		Map<String, ProtoMessage> messages = new HashMap<String, ProtoMessage>();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				int start = line.indexOf(HEAD);
				int end = line.indexOf(';');
				if (start > -1 && end > -1) {
					line = line.substring(start, end).trim();
					String[] infos = line.split("=");
					ProtoMessage protoMessage = new ProtoMessage();
					if (infos[0].startsWith(REQUEST)) {
						
					} else if (infos[0].startsWith(RESPONSE)) {
						
					} else {
						throw new Exception("Unknow line : " + line);
					}
					protoMessage.setMessageIdName(infos[0]);
					protoMessage.setMessageIdValue(Integer.parseInt(infos[1]));
					messages.put(infos[0], protoMessage);
				}
			}
		} finally {
			reader.close();
		}
		return messages;
	}

}
