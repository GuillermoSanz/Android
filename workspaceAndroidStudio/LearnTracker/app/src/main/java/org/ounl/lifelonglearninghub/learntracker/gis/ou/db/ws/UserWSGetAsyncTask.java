/*******************************************************************************
 * Copyright (C) 2014 Open University of The Netherlands
 * Author: Bernardo Tabuenca Archilla
 * LearnTracker project 
 * 
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.ounl.lifelonglearninghub.learntracker.gis.ou.db.ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ounl.lifelonglearninghub.learntracker.gis.ou.db.ws.dataobjects.UserDO;
import org.ounl.lifelonglearninghub.learntracker.gis.ou.db.ws.dataobjects.UserDOList;
import org.ounl.lifelonglearninghub.learntracker.gis.ou.session.Session;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

public class UserWSGetAsyncTask extends
		AsyncTask<String, Integer, List<UserDO>> {
	
	private String CLASSNAME = this.getClass().getName();


	public List<UserDO> listUsers(String sCourseId) {
		HttpURLConnection c = null;

		String url = Session.getSingleInstance().getWSPath()+"/_ah/api/userendpoint/v1/user/course/";
		url+=sCourseId;

		try {
			URL u = new URL(url);
			c = (HttpURLConnection) u.openConnection();
			c.setRequestMethod("GET");
			c.setRequestProperty("Content-length", "0");
			c.setUseCaches(false);
			c.setAllowUserInteraction(false);
			c.setConnectTimeout(5000);
			c.setReadTimeout(5000);
			c.connect();
			int status = c.getResponseCode();


			switch (status) {
				case 200:
				case 201:
					BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = br.readLine()) != null) {
						sb.append(line+"\n");
					}
					br.close();
					Gson gson = new Gson();
					UserDOList r = gson.fromJson(sb.toString(), UserDOList.class);
					return r.users;
			}

		} catch (MalformedURLException ex) {
			Log.e(CLASSNAME, "Error in http connection " + ex.toString());
		} catch (IOException ex) {
			Log.e(CLASSNAME, "Error in http connection " + ex.toString());
		} finally {
			if (c != null) {
				try {
					c.disconnect();
				} catch (Exception ex) {
					Log.e(CLASSNAME, "Error in http connection " + ex.toString());
				}
			}
		}




		return null;
	}

/*
	public List<UserDO> listUsers(String sCourseId) {


		InputStream is = null;
		String url = Session.getSingleInstance().getWSPath()+"/_ah/api/userendpoint/v1/user/course/";
		url+=sCourseId;

		try {
			Log.d(CLASSNAME, " Querying to backend " + url);
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);

			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w(getClass().getSimpleName(), "Error " + statusCode + " for URL " + url);
				return null;
			}

			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {
			Log.e(CLASSNAME, "Error in http connection " + e.toString());
		}



		Reader reader = new InputStreamReader(is);
		Gson gson = new Gson();
		UserDOList r = gson.fromJson(reader, UserDOList.class);
		
		
		return r.users;
		
	}
*/

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}

	@Override
	protected List<UserDO> doInBackground(String... sParam) {

		Log.d(CLASSNAME, "Starting task in background...");
		List<UserDO> luOutList = new ArrayList<UserDO>();

		try {

			Log.d(CLASSNAME, "Course ID [" + sParam[0].toString() + "]");
			luOutList = listUsers(sParam[0].toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return luOutList;
	}

	@Override
	protected void onPostExecute(List<UserDO> result) {
		super.onPostExecute(result);

	}


	

}
