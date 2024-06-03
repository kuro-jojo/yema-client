package com.kuro.yema.repositories;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.algolia.search.DefaultSearchClient;
import com.algolia.search.SearchClient;
import com.algolia.search.SearchIndex;
import com.algolia.search.models.indexing.Query;
import com.algolia.search.models.indexing.SearchResult;
import com.algolia.search.models.settings.IndexSettings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kuro.yema.BuildConfig;
import com.kuro.yema.data.enums.Country;
import com.kuro.yema.data.enums.HouseType;
import com.kuro.yema.data.enums.ListingType;
import com.kuro.yema.data.enums.RentalTerm;
import com.kuro.yema.data.model.House;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The type House repository.
 */
public class HouseRepository {
    private final String INDEX_NAME = "yema";
    private final OnFirestoreTaskComplete onFirestoreTaskComplete;
    private final SearchIndex<House> algoliaSearchIndex;
    private final CollectionReference mRootCollection;
    private final FirebaseFirestore mFirestore;

    /**
     * Instantiates a new House repository.
     *
     * @param onFirestoreTaskComplete the on firestore task complete
     */
    public HouseRepository(OnFirestoreTaskComplete onFirestoreTaskComplete) {
        mFirestore = FirebaseFirestore.getInstance();
        this.onFirestoreTaskComplete = onFirestoreTaskComplete;
        mRootCollection = mFirestore.collection("houses");

        // algolia configuration
        SearchClient algoliaSearchClient = DefaultSearchClient.create(BuildConfig.ALGOLIA_APP_KEY, BuildConfig.ALGOLIA_API_KEY);
        algoliaSearchIndex = algoliaSearchClient.initIndex(INDEX_NAME, House.class);
        algoliaSearchIndex.setSettingsAsync(
                new IndexSettings().setSearchableAttributes(Collections.singletonList(
                        "location"
                ))
        );
    }

    /**
     * Gets house by house id.
     *
     * @param houseId the house id
     */
    public void getHouseByHouseId(String houseId) {
        getHouse("objectID:" + houseId);
    }

    /**
     * Gets houses in list of ids.
     *
     * @param houseIds the house ids
     */
    public void getHousesInListOfIds(ArrayList<String> houseIds) {
        CompletableFuture.runAsync(() -> {
            try {
                algoliaSearchIndex.getObjectsAsync(houseIds).join();
                List<House> results = algoliaSearchIndex.getObjectsAsync(houseIds).join();
                new Handler(Looper.getMainLooper()).post(() -> {
                    onFirestoreTaskComplete.housesDataLoaded(results);
                });
            } catch (Exception e) {
                onFirestoreTaskComplete.onError(e);
                Log.e("ALGOLIA", "err : " + e.getMessage());
            }
        });
    }


    private void getHouse(String filter) {
        CompletableFuture.runAsync(() -> {
            try {
                SearchResult<House> result = algoliaSearchIndex.searchAsync(new Query()
                                .setFilters(filter))
                        .join();
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!result.getHits().isEmpty()) {
                        onFirestoreTaskComplete.houseDataLoaded(result.getHits().get(0));
                    }
                });
            } catch (Exception e) {
                onFirestoreTaskComplete.onError(e);
                Log.e("ALGOLIA", "err : " + e.getMessage());
            }
        });
    }

    /**
     * <p>retrieve list of houses using algolia with filters</p>
     * <ul>
     * <li>"price<=10" -> numerical filter</li>
     * <li>"price:10 TO 20" -> numerical filter  : range of value</li>
     * <li>"name:doe" -> string filter : equality</li>
     * <li>"NOT name:doe" -> string filter : inequality</li>
     * <li>"objectID:id" -> filter on objectID</li>
     * </ul>
     */
    public void getHouses(String filter, String query) {
        CompletableFuture.runAsync(() -> {
            try {
                SearchResult<House> results = algoliaSearchIndex.searchAsync(new Query(query)
                                .setFilters(filter))
                        .join();

                new Handler(Looper.getMainLooper()).post(() -> {
                    onFirestoreTaskComplete.housesDataLoaded(results.getHits());
                });
            } catch (Exception e) {
                onFirestoreTaskComplete.onError(e);
                Log.e("ALGOLIA", "err : " + e.getMessage());
            }
        });
    }

    /**
     * Gets all houses.
     */
    public void getAllHouses() {
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        for (HouseType houseType : HouseType.values()) {
            Task<QuerySnapshot> task = mFirestore.collectionGroup(houseType.typeLowerCase).get();
            tasks.add(task);
        }

        getAllHouses(tasks);
    }

    private void getAllHouses(List<Task<QuerySnapshot>> tasks) {

        Tasks.whenAllComplete(tasks).addOnCompleteListener(allTasks -> {
            List<House> houses = new ArrayList<>();

            for (Task<?> task : allTasks.getResult()) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = (QuerySnapshot) task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        houses.addAll(querySnapshot.toObjects(House.class));
                    }
                } else {
                    onFirestoreTaskComplete.onError(task.getException());
                }
            }

            try {
                onFirestoreTaskComplete.housesDataLoaded(houses);
            } catch (RuntimeException e) {
                onFirestoreTaskComplete.onRuntimeError(e);
                Log.e("AndroidRuntime", "err : " + e.getMessage());
            }
        });
    }

    /**
     * Gets all houses by country.
     *
     * @param country the country
     */
// TODO: USEFUL
    public void getAllHousesByCountry(Country country) {

        for (HouseType houseType : HouseType.values()) {
            mRootCollection.document(country.name).collection(houseType.typeLowerCase).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        try {
                            onFirestoreTaskComplete.housesDataLoaded(task.getResult().toObjects(House.class));

                        } catch (RuntimeException e) {
                            onFirestoreTaskComplete.onRuntimeError(e);
                        }
                    } else {
                        onFirestoreTaskComplete.onError(task.getException());
                    }

                }
            });
        }

    }

    /**
     * Gets all houses by country by house type.
     *
     * @param country   the country
     * @param houseType the house type
     */
    public void getAllHousesByCountryByHouseType(Country country, HouseType houseType) {

        mRootCollection.document(country.name).collection(houseType.type)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        try {
                            onFirestoreTaskComplete.housesDataLoaded(task.getResult().toObjects(House.class));

                        } catch (RuntimeException e) {
                            onFirestoreTaskComplete.onRuntimeError(e);
                        }
                    } else {
                        onFirestoreTaskComplete.onError(task.getException());
                    }
                });
    }


    /**
     * Gets new houses.
     *
     * @param nDaysAgo the n days ago
     */
    public void getNewHouses(int nDaysAgo) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -nDaysAgo);
        Date daysAgo = calendar.getTime();

        Timestamp daysAgoTimestamp = new Timestamp(daysAgo);
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        for (HouseType houseType : HouseType.values()) {
            Task<QuerySnapshot> task = mFirestore.collectionGroup(houseType.typeLowerCase)
                    .whereGreaterThan("uploadedAt", daysAgoTimestamp).get();
            tasks.add(task);
        }

        getAllHouses(tasks);
    }

    /**
     * Gets houses by the house type.
     *
     * @param houseType the house type
     */
    public void getHousesByHouseType(HouseType houseType) {
        mFirestore.collectionGroup(houseType.typeLowerCase).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                try {
                    onFirestoreTaskComplete.housesDataLoaded(task.getResult().toObjects(House.class));
                } catch (RuntimeException e) {
                    onFirestoreTaskComplete.onRuntimeError(e);
                }
            } else {
                onFirestoreTaskComplete.onError(task.getException());
            }
        });
    }

    /**
     * Gets houses by the listing type (rent/sale).
     *
     * @param listingType the listing type
     */
    public void getHousesByListingType(ListingType listingType) {
        getHouses("listingType:" + listingType.toString(), "");
    }

    /**
     * Gets houses by the rental term (month/year).
     *
     * @param rentalTerm the rental term
     */
    public void getHousesByRentalTerm(RentalTerm rentalTerm) {
        getHouses("rentalTerm:" + rentalTerm.toString(), "");
    }

    /**
     * Gets houses by location and price.
     *
     * @param houseLocation the house location
     * @param housePrice    the house price
     */
    public void getHousesByLocationAndPrice(String houseLocation, String housePrice) {
        getHouses("price <= " + housePrice, houseLocation);
    }

    /**
     * Gets houses by location.
     *
     * @param houseLocation the house location
     */
    public void getHousesByLocation(String houseLocation) {
        getHouses("", houseLocation);
    }

    /**
     * Gets houses by price.
     *
     * @param housePrice the house price
     */
    public void getHousesByPrice(String housePrice) {
        getHouses("price<=" + housePrice, "");
    }

    /**
     * Gets houses by the budget (a price range).
     *
     * @param minPrice the min price
     * @param maxPrice the max price
     */
    public void getHousesByPriceRange(double minPrice, double maxPrice) {
        getHouses("price:" + minPrice + " TO " + maxPrice, "");
    }

    /**
     * Gets houses that have exactly numberBedrooms bedrooms.
     *
     * @param numberBedrooms the number bedrooms
     */
    public void getHousesWithExactBedrooms(int numberBedrooms) {
        getHouses("numberBedrooms=" + numberBedrooms, "");
    }

    /**
     * Gets houses that have at most numberBedrooms bedrooms.
     *
     * @param numberBedrooms the number bedrooms
     */
    public void getHousesWithAtMostBedrooms(int numberBedrooms) {
        getHouses("numberBedrooms<=" + numberBedrooms, "");
    }

    /**
     * Gets houses that have at least numberBedrooms bedrooms.
     *
     * @param numberBedrooms the number bedrooms
     */
    public void getHousesWithAtLeastBedrooms(int numberBedrooms) {
        getHouses("numberBedrooms>=" + numberBedrooms, "");
    }

    /**
     * Gets houses that have exactly numberBathrooms bathrooms.
     *
     * @param numberBathrooms the number bathrooms
     */
    public void getHousesWithExactBathrooms(int numberBathrooms) {
        getHouses("numberBathrooms=" + numberBathrooms, "");
    }

    /**
     * Gets houses that have at most numberBathrooms bathrooms.
     *
     * @param numberBathrooms the number bathrooms
     */
    public void getHousesWithAtMostBathrooms(int numberBathrooms) {
        getHouses("numberBathrooms<=" + numberBathrooms, "");
    }

    /**
     * Gets houses that have at least numberBathrooms bathrooms.
     *
     * @param numberBathrooms the number bathrooms
     */
    public void getHousesWithAtLeastBathrooms(int numberBathrooms) {
        getHouses("numberBathrooms>=" + numberBathrooms, "");
    }

    /**
     * Gets houses that have exactly numberParkingSpot ParkingSpot.
     *
     * @param numberParkingSpot the number ParkingSpot
     */
    public void getHousesWithExactParkingSpot(int numberParkingSpot) {
        getHouses("numberParkingSpot=" + numberParkingSpot, "");
    }

    /**
     * Gets houses that have at most numberParkingSpot ParkingSpot.
     *
     * @param numberParkingSpot the number ParkingSpot
     */
    public void getHousesWithAtMostParkingSpot(int numberParkingSpot) {
        getHouses("numberParkingSpot<=" + numberParkingSpot, "");
    }

    /**
     * Gets houses that have at least numberParkingSpot ParkingSpot.
     *
     * @param numberParkingSpot the number ParkingSpot
     */
    public void getHousesWithAtLeastParkingSpot(int numberParkingSpot) {
        getHouses("numberParkingSpot>=" + numberParkingSpot, "");
    }

    /**
     * The interface On firestore task complete.
     */
    public interface OnFirestoreTaskComplete {
        void housesDataLoaded(List<House> houseList);

        void houseDataLoaded(House house);

        void onError(Exception e);

        void onRuntimeError(RuntimeException e);
    }
}
