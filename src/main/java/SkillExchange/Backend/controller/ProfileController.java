package SkillExchange.Backend.controller;

import SkillExchange.Backend.config.JwtUtil;
import SkillExchange.Backend.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private JwtUtil jwtUtil;

    // get email from token
    private String getEmail(String header) {
        String token = header.substring(7);
        return jwtUtil.getEmail(token);
    }


    @PostMapping("/photo")
    public Map<String, String> uploadPhoto(
            @RequestHeader("Authorization") String header,
            @RequestParam("file") MultipartFile file)
            throws IOException {

        String email = getEmail(header);
        String url = cloudinaryService
                .uploadProfilePhoto(email, file);

        return Map.of("photoUrl", url);
    }

    // DELETE /api/profile/photo
    // remove profile photo from Cloudinary and clear DB URL
    @DeleteMapping("/photo")
    public String deletePhoto(
            @RequestHeader("Authorization") String header)
            throws IOException {

        String email = getEmail(header);
        return cloudinaryService.deleteProfilePhoto(email);
    }


    @GetMapping("/photo")
    public Map<String, String> getPhoto(
            @RequestHeader("Authorization") String header) {

        String email = getEmail(header);
        String url = cloudinaryService.getPhotoUrl(email);

        return Map.of(
                "photoUrl",
                url != null ? url : ""
        );
    }
}