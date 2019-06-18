package network;

import data.Result;

import java.util.List;

import data.CardItem;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface CalendarService {

    @GET("/api/v1/scratchable")
    Observable<Result<List<CardItem>>> getCardServices(
            @Query("im") String imei
    );
}
