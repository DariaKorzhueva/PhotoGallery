package android.bignerdranch.com.photogallery;

import java.util.List;

public class PhotoRequestResult {
    PhotoResults photos;
    String stat;

    List<GalleryItem> getResults() {
        return photos.getPhotolist();
    }

    int getPageCount() {
        return photos.getMaxPages();
    }

    int getItemCount() {
        return photos.getTotal();
    }

    int getItemsPerPage() {
        return photos.getItemsPerPage();
    }
}
