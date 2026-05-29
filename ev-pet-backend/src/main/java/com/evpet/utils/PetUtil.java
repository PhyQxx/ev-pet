package com.evpet.utils;

import com.evpet.model.Pet;
import com.evpet.vo.PetVO;

public final class PetUtil {

    private PetUtil() {}

    public static String calculateStatus(Pet pet) {
        if (pet.getMood() < 30) return "angry";
        if (pet.getFullness() < 30) return "hungry";
        if (pet.getHealth() < 30) return "tired";
        return "happy";
    }

    public static Long calculateNextEvolutionExp(int stage) {
        return switch (stage) {
            case 1 -> 100L;
            case 2 -> 500L;
            default -> 0L;
        };
    }

    public static PetVO toPetVO(Pet pet) {
        return PetVO.builder()
                .id(pet.getId())
                .name(pet.getName())
                .stage(pet.getStage())
                .level(pet.getLevel())
                .exp(pet.getExp())
                .health(pet.getHealth())
                .fullness(pet.getFullness())
                .mood(pet.getMood())
                .status(calculateStatus(pet))
                .appearance(pet.getAppearance())
                .nextEvolutionExp(calculateNextEvolutionExp(pet.getStage()))
                .build();
    }
}
