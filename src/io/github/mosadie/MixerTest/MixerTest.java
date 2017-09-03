package io.github.mosadie.MixerTest;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mixer.api.MixerAPI;
import com.mixer.interactive.GameClient;

public class MixerTest {

	public static final String client_id = "91bdf6ea960a60b62b68bb964767022ffe8b7f1b81e3fe0c";

	public static MixerAPI mixer;
	public static GameClient gameClient;

	public MixerTest() {

	}

	public static void main(String[] args) {
		JSONObject json = new JSONObject();
		json.put("client_id", client_id);
		json.put("scope", "interactive:robot:self");
		boolean finished = false;
		try {
			String result = Request.Post("https://mixer.com/api/v1/oauth/shortcode").bodyString(json.toJSONString(), ContentType.APPLICATION_JSON).execute().returnContent().asString();
			JSONParser parser = new JSONParser();
			try {
				JSONObject resultJSON = (JSONObject) parser.parse(result);
				if (!resultJSON.containsKey("code")) {
					finished = true;
					System.out.println("Something went wrong, please try again.");
					return;
				}
				String handle = (String) resultJSON.get("handle");
				String code = (String) resultJSON.get("code");
				double time = Double.parseDouble(((Long) resultJSON.get("expires_in")).toString());
				System.out.println("----------------------------------------------------");
				System.out.println("Please go to https://mixer.com/go and type in the code: " + code);
				System.out.println("----------------------------------------------------");
				while (!finished && time > 0) {
					Content content = Request.Get("https://mixer.com/api/v1/oauth/shortcode/check/"+handle).execute().returnContent();
					String response;
					if (content == null) { 
						response = "{\"statusCode\":204}";
					}
					else {
						response = content.asString();
					}
					JSONObject codeJSON = (JSONObject) parser.parse(response);
					long statusCode;
					if (content == null) {
						statusCode = (Long) codeJSON.get("statusCode");
					}
					else {
						statusCode = 200;
					}
					if (statusCode == 200L) {
						if (codeJSON.containsKey("code")) {
							finished = true;
							System.out.println("OAuth token token received!");
							String AuthCode = (String) codeJSON.get("code");
							JSONObject tokenJSON = new JSONObject();
							tokenJSON.put("grant_type", "authorization_code");
							tokenJSON.put("client_id", client_id);
							tokenJSON.put("code", AuthCode);
							//tokenJSON.put("redirect_uri", "https://mosadie.github.io/MixBukkitSuccess");
							ResponseHandler<String> rh = new ResponseHandler<String>() {
								public String handleResponse(HttpResponse response)
										throws ClientProtocolException, IOException {
									HttpEntity entity = response.getEntity();
									if (entity == null) {
										throw new ClientProtocolException("Response contains no content");
									}
									String output = IOUtils.toString(entity.getContent());
									System.out.println("OauthJSON: "+output);
									return output;
								}
							};
							String oauthJSON = Request.Post("https://mixer.com/api/v1/oauth/token").bodyString(tokenJSON.toJSONString(), ContentType.APPLICATION_JSON).execute().handleResponse(rh);
							//Response debugresponse = Request.Post("http://mixer.com/api/v1/oauth/token").bodyString(tokenJSON.toJSONString(), ContentType.APPLICATION_JSON).execute();
							//String oauthJSON = response.returnContent().asString();
							JSONObject OAuthJson = (JSONObject) parser.parse(oauthJSON);
							if (!OAuthJson.containsKey("access_token")) {
								System.out.println("Something went wrong");
								return;
							}
							String OAuth =(String) OAuthJson.get("access_token");
							mixer = new MixerAPI(OAuth);
							System.out.println("Setup of Mixer API finished!");
							gameClient = new GameClient(109628);
							gameClient.connect(OAuth,"5owte2yt");
							gameClient.getEventBus().register(new EventHandler()); //This is where the class is registered.
							gameClient.ready(true);
							System.out.println("Setup of Game Client finished!");
							System.out.println("All Done!");
							finished=true;
							return;
						} else {
							finished = true;
							return;
						}
					} else if (statusCode != 204L) {
						finished = true;
						System.out.println("Something went wrong, please try again.");
						return;
					}
					time -= .5;
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (time <= 0) {
						System.out.println("Please ignore the previous code, it has now expired. Restart the program to try again.");
						System.exit(0);
						return;
					}
				}
				finished = true;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Something went wrong, please try again.");
		return;

	}
}
