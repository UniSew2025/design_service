package com.unisew.design_service.service;

import com.unisew.design_service.models.Cloth;
import com.unisew.design_service.models.DesignDraft;
import com.unisew.design_service.models.DraftImage;
import com.unisew.design_service.repositories.ClothRepo;
import com.unisew.design_service.repositories.DesignDraftRepo;
import com.unisew.design_service.repositories.DraftImageRepo;
import com.unisew.design_service.request.CreateDesignDraftRequest;
import com.unisew.design_service.request.SetDesignDraftFinalRequest;
import com.unisew.design_service.response.ResponseObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DesignDraftServiceImpl implements DesignDraftService {

    final ClothRepo clothRepo;
    final DesignDraftRepo designDraftRepo;
    final DraftImageRepo draftImageRepo;

    @Override
    public ResponseEntity<ResponseObject> createDesignDraft(CreateDesignDraftRequest request) {

        Cloth cloth = clothRepo.findById(request.getClothId()).orElse(null);


        if (cloth == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Can not find cloth with id " + request.getClothId())
                            .build()
            );
        }

        DesignDraft designDraft = DesignDraft.builder()
                .cloth(cloth)
                .description(request.getDescriptions())
                .designDate(LocalDate.now())
                .isFinal(false)
                .build();
        designDraftRepo.save(designDraft);

        List<DraftImage> draftImages = request.getImages().stream()
                .map(image -> DraftImage.builder()
                        .imageUrl(image.getUrl())
                        .name(image.getName())
                        .designDraft(designDraft)
                        .build()
                ).toList();
        draftImageRepo.saveAll(draftImages);

        designDraft.setDraftImages(draftImages);
        designDraftRepo.save(designDraft);


        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.builder()
                        .message("Upload Design draft successful")
                        .build()
        );
    }

    @Override
    public ResponseEntity<ResponseObject> setDesignDraftFinal(SetDesignDraftFinalRequest request) {

        DesignDraft designDraft = designDraftRepo.findById(request.getDesignDraftId()).orElse(null);

        if (designDraft == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseObject.builder()
                            .message("Can not find design draft with id " + request.getDesignDraftId())
                            .build()
            );
        }

            designDraft.setFinal(true);
            designDraftRepo.save(designDraft);

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Make design draft final successful")
                        .build()
        );
    }
}
