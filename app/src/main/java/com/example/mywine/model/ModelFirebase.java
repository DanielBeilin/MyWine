package com.example.mywine.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.example.mywine.model.Comment.Comment;
import com.example.mywine.model.Post.Post;
import com.example.mywine.model.User.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    public static final String USERS_COLLECTION_NAME = "users";
    public static final String POSTS_COLLECTION_NAME = "Posts";
    public static final String USERS_IMAGE_FOLDER = "users_images/";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();

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

    public interface getPostsByUserIDListener{
        void onComplete(List<Post> list);
    }

    public interface getAllCommentsListener{
        void onComplete(List<Comment> list);
    }

    public interface SignInOnFailureListener {
        void onComplete(String errorMessage);
    }

    public interface SignInOnSuccessListener {
        void onComplete();
    }

    public void getAllPosts(Long lastUpdateDate, getAllPostsListener listener){
        db.collection(POSTS_COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("updateDate",new Timestamp(lastUpdateDate,0))
                .whereEqualTo("isDeleted",false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                           List<Post> list = new LinkedList<Post>();
                           if (task.isSuccessful()) {
                               for (QueryDocumentSnapshot doc : task.getResult()) {
                                   Post post = Post.create(doc.getData());
                                   list.add(post);
                               }
                           }
                           listener.onComplete(list);
                       }
                   });
    }

    public void getAllUsers(Long lastUpdateDate, getAllUsersListener listener){
        db.collection(USERS_COLLECTION_NAME)
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

//    public void getAllComments(Long lastUpdateDate, getAllCommentsListener listener){
//        db.collection("Posts/comments")
//                .whereGreaterThanOrEqualTo("updateDate",new Timestamp(lastUpdateDate,0))
//                .get()
//                .addOnCompleteListener(task -> {
//                    List<Comment> list = new LinkedList<Comment>();
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot doc : task.getResult()){
//                            Comment comment = Comment.create(doc.getData());
//                            list.add(comment);
//                        }
//                    }
//                    listener.onComplete(list);
//                });
//    }

    public void getPostById(String postId, PostModelStorageFunctions.GetPostById listener) {
        db.collection(POSTS_COLLECTION_NAME)
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
        db.collection(USERS_COLLECTION_NAME)
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user = null;
                        if(task.isSuccessful() & task.getResult() != null){
                            user = User.create((task.getResult().getData()));
                        }
                        listener.onComplete(user);
                    }
                });
    }

    public void getUserNameById(String userId, UserModelStorageFunctions.GetNameByUserId listener) {
        db.collection(USERS_COLLECTION_NAME)
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user = null;
                        String userName = "";
                        if(task.isSuccessful() & task.getResult() != null){
                            user = User.create(Objects.requireNonNull(task.getResult().getData()));
                            userName = user.getFullName();
                        }
                        listener.onComplete(userName);
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
        db.collection(POSTS_COLLECTION_NAME)
                .document(postId)
                .collection("comments")
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

    public void getPostsByUserId(String userId,Long lastUpdateDate, getPostsByUserIDListener listener) {
        db.collection(POSTS_COLLECTION_NAME)
            .whereGreaterThanOrEqualTo("updateDate",new Timestamp(lastUpdateDate,0))
            .whereEqualTo("userId",userId)
            .whereEqualTo("isDeleted",false)
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

    public void deletePost(Post post, PostModelStorageFunctions.deletePostListener listener){

    }

    public void addUser(User user,String password, UserModelStorageFunctions.addUserListener listener) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(),password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            user.setUid(task.getResult().getUser().getUid());
                            Map<String, Object> json = user.toJson();
                            db.collection(USERS_COLLECTION_NAME)
                                    .document(task.getResult().getUser().getUid())
                                    .set(json)
                                    .addOnSuccessListener(unused -> listener.onComplete())
                                    .addOnFailureListener(e -> listener.onComplete());
                        }
                    }
                });
    }

    public void updateUser(User user, UserModelStorageFunctions.addUserListener listener) {
        Map<String, Object> jsonUser = user.toJson();
        db.collection(USERS_COLLECTION_NAME)
                .document(user.getUid())
                .update(jsonUser)
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());
    }

    public void signIn(String email, String password, SignInOnSuccessListener onSuccessListener, SignInOnFailureListener onFailureListener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(task -> {
                    onSuccessListener.onComplete();
                })
                .addOnFailureListener(command -> {
                    onFailureListener.onComplete(command.getMessage());
                });
    }

    public void addPost(Post post, PostModelStorageFunctions.addPostListener listener) {
        Map<String, Object> json = post.toJson();
        db.collection(POSTS_COLLECTION_NAME).document(post.getUid())
                .set(json)
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());
    }

    public void addComment(Comment comment, CommentModelStorageFunctions.addCommentListener listener) {
        Map<String, Object> json = comment.toJson();
        db.collection(POSTS_COLLECTION_NAME)
                .document(comment.getPostId())
                .collection("comments")
                .document(comment.getUid()).set(json)
                .addOnSuccessListener(unused -> listener.onComplete())
                .addOnFailureListener(e -> listener.onComplete());
    }

    public void uploadUserPhoto(Bitmap imageBmp, String name, UserModelStorageFunctions.UploadUserPhotoListener listener){
        final StorageReference imagesRef = storage.getReference().child(USERS_IMAGE_FOLDER).child(name);

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArray);
        byte[] data = byteArray.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> listener.onComplete(null))
                .addOnSuccessListener(taskSnapshot -> imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    listener.onComplete(uri.toString());
                }));
    };

    public void saveUserImage(Bitmap imageBitmap,String uid, String imageName , UserModelStorageFunctions.saveUserImageListener listener ) {
        StorageReference storageRef = storage.getReference();
        StorageReference imgRef = storageRef.child(String.format("users_images/%s",imageName));

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

    public void savePostImage(Bitmap imageBitmap, String imageName , PostModelStorageFunctions.savePostImageListener listener ) {
        StorageReference storageRef = storage.getReference();
        StorageReference imgRef = storageRef.child(String.format("post_images/%s",imageName));

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

    public FirebaseUser getLoggedInUser() {
        return (mAuth.getCurrentUser());
    }

    public void signUserOut() { mAuth.signOut(); }
}
