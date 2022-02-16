package com.example.mywine.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.example.mywine.model.Comment.Comment;
import com.example.mywine.model.Post.Post;
import com.example.mywine.model.User.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ModelFirebase {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ModelFirebase() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();

        db.setFirestoreSettings(settings);
    }

    public interface getAllUsersListener{
        void onComplete(List<User> list);
    }

    public interface getAllPostsListener{
        void onComplete(List<Post> list);
    }

    public interface getAllCommentsListener{
        void onComplete(List<Comment> list);
    }

    public void getAllPosts(Long lastUpdateDate, getAllPostsListener listener){
        db.collection(Post.COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("updateDate",new Timestamp(lastUpdateDate,0))
                .get()
                .addOnCompleteListener(task -> {
                    List<Post> list = new LinkedList<Post>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()){
                            Post post = Post.create(doc.getData());
                            list.add(post);
                        }
                    }
                    listener.onComplete(list);
                });
    }

    public void getAllUsers(Long lastUpdateDate, getAllUsersListener listener){
        db.collection(User.COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("updateDate",new Timestamp(lastUpdateDate,0))
                .get()
                .addOnCompleteListener(task -> {
                    List<User> list = new LinkedList<User>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()){
                            User user = User.create(doc.getData());
                            list.add(user);
                        }
                    }
                    listener.onComplete(list);
                });
    }

    public void getAllComments(Long lastUpdateDate, getAllCommentsListener listener){
        db.collection(Comment.COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("updateDate",new Timestamp(lastUpdateDate,0))
                .get()
                .addOnCompleteListener(task -> {
                    List<Comment> list = new LinkedList<Comment>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()){
                            Comment comment = Comment.create(doc.getData());
                            list.add(comment);
                        }
                    }
                    listener.onComplete(list);
                });
    }

    public void getPostById(String postId, PostModelStorageFunctions.GetPostById listener) {
        db.collection(Post.COLLECTION_NAME)
                .document(postId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Post post = null;
                        if(task.isSuccessful() & task.getResult() != null){
                            post = Post.create(Objects.requireNonNull(task.getResult().getData()));
                        }
                        listener.onComplete(post);
                    }
                });
    }

    public void getUserById(String userId, UserModelStorageFunctions.GetUserById listener) {
        db.collection(User.COLLECTION_NAME)
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user = null;
                        if(task.isSuccessful() & task.getResult() != null){
                            user = User.create(Objects.requireNonNull(task.getResult().getData()));
                        }
                        listener.onComplete(user);
                    }
                });
    }

    public void getCommentById(String commentId, CommentModelStorageFunctions.GetCommentById listener) {
        db.collection(Comment.COLLECTION_NAME)
                .document(commentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Comment comment = null;
                        if(task.isSuccessful() & task.getResult() != null){
                            comment = Comment.create(Objects.requireNonNull(task.getResult().getData()));
                        }
                        listener.onComplete(comment);
                    }
                });

    }

    public void getCommentsByPostId(String postId, CommentModelStorageFunctions.GetCommentsByPostId listener) {
        db.collection(Comment.COLLECTION_NAME)
                .whereEqualTo("postId",postId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Comment> commentList = new ArrayList<Comment>();
                        if (task.isSuccessful() & task.getResult() != null){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                commentList.add(Comment.create(document.getData()));
                            }
                        }
                        listener.onComplete(commentList);
                    }
                });
    }

    public void getPostsByUserId(String userId, PostModelStorageFunctions.getPostsByUserID listener) {
        db.collection(Post.COLLECTION_NAME)
            .whereEqualTo("userId",userId)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<Post> postList = new ArrayList<Post>();
                    if (task.isSuccessful() & task.getResult() != null){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            postList.add(Post.create(document.getData()));
                        }
                    }
                    listener.onComplete(postList);

                }
            });
    }

    public void addPost(Post post, PostModelStorageFunctions.addPostListener listener) {
        Map<String, Object> json = post.toJson();
        db.collection(Post.COLLECTION_NAME)
                .document(post.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Post post = null;
                        if(task.isSuccessful() & task.getResult() != null) {
                            post = Post.create(task.getResult().getData());
                        }
                        listener.onComplete();
                    }
                });
    }

    public void addComment(Comment comment, CommentModelStorageFunctions.addCommentListener listener) {
        Map<String, Object> json = comment.toJson();
        db.collection(Post.COLLECTION_NAME)
                .document(comment.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Comment comment = null;
                        if(task.isSuccessful() & task.getResult() != null) {
                            comment = Comment.create(task.getResult().getData());
                        }
                        listener.onComplete();
                    }
                });
    }


    FirebaseStorage storage = FirebaseStorage.getInstance();

    public void saveImage(Bitmap imageBitmap, String imageName, String imgType , ImageStorageFunctions.SaveImageListener listener ) {
        StorageReference storageRef = storage.getReference();
        StorageReference imgRef = storageRef.child(String.format("%s_images/%s",imgType,imageName));

        ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteOutPutStream);
        byte[] data = byteOutPutStream.toByteArray();

        UploadTask uploadTask = imgRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> listener.onComplete(null))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            listener.onComplete(uri.toString());
                        });
                    }
                });
    }

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public boolean isSignedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return (currentUser != null);
    }
}
