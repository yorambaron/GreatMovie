package com.yboweb.bestmovie;

/**
 * Created by test on 10/04/16.
 */
public class ActorDetails  {
// Test 1235

   void ActorDetails() {

   }
    protected String readActorDetails(String id) {

        ReadURL readURL = new ReadURL();

        ImdbConstants imdbConstObject = ImdbConstants.getInstance();
        String url = imdbConstObject.getActorDetails(id);
        StringBuffer bufferRead = readURL.Read(url);
        return (bufferRead.toString());
    }

}

