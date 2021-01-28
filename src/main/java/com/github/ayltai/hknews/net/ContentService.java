package com.github.ayltai.hknews.net;

import com.github.ayltai.hknews.data.model.RssFeed;

import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ContentService {
    @NotNull
    @GET
    Call<RssFeed> getFeed(@NotNull @Url String url);

    @NotNull
    @GET
    Call<String> getHtml(@NotNull @Url String url);

    @NotNull
    @FormUrlEncoded
    @POST
    Call<String> postHtml(@NotNull @Url String url, @Field("sid") int sectionId, @Field("p") int page);
}
