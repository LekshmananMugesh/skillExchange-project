package SkillExchange.Backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import SkillExchange.Backend.model.User;
import SkillExchange.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private UserRepository userRepository;

    // ── UPLOAD PROFILE PHOTO ─────────────────────────────
    // accepts the multipart file from the controller
    // uploads to Cloudinary under the skillexchange/profiles folder
    // saves the returned CDN URL into the users table
    // returns the public URL so the frontend can update the avatar
    public String uploadProfilePhoto(String email,
                                     MultipartFile file)
            throws IOException {

        User user = userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("User not found"));

        // upload to Cloudinary
        // folder keeps all profile photos organised
        // use_filename preserves original name
        // unique_filename prevents collisions
        Map uploadResult = cloudinary.uploader().upload(
            file.getBytes(),
            ObjectUtils.asMap(
                "folder",          "skillexchange/profiles",
                "public_id",       "user_" + user.getId(),
                "overwrite",       true,
                "use_filename",    false,
                "unique_filename", false,
                "resource_type",   "image"
            )
        );

        // Cloudinary returns the secure HTTPS CDN URL
        String photoUrl = (String) uploadResult
            .get("secure_url");

        // persist URL in the users table
        user.setPhotoUrl(photoUrl);
        userRepository.save(user);

        return photoUrl;
    }

    // ── DELETE PROFILE PHOTO ─────────────────────────────
    // removes the image from Cloudinary storage
    // clears the URL from the users table
    // called when user wants to reset to initials avatar
    public String deleteProfilePhoto(String email)
            throws IOException {

        User user = userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("User not found"));

        // destroy the image using the deterministic public_id
        cloudinary.uploader().destroy(
            "skillexchange/profiles/user_" + user.getId(),
            ObjectUtils.emptyMap()
        );

        // clear the URL
        user.setPhotoUrl(null);
        userRepository.save(user);

        return "Photo deleted successfully";
    }

    // ── GET PHOTO URL ────────────────────────────────────
    // simple fetch — returns current photo URL or null
    // frontend uses null to show initials fallback
    public String getPhotoUrl(String email) {
        User user = userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("User not found"));

        return user.getPhotoUrl();
    }
}