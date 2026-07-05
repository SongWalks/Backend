package com.sookmyung.swapclass.domain.block.service;

import com.sookmyung.swapclass.domain.block.dto.response.BlockResponse;
import com.sookmyung.swapclass.domain.block.entity.UserBlock;
import com.sookmyung.swapclass.domain.block.repository.UserBlockRepository;
import com.sookmyung.swapclass.domain.user.entity.User;
import com.sookmyung.swapclass.domain.user.repository.UserRepository;
import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockService {

    private final UserBlockRepository userBlockRepository;
    private final UserRepository userRepository;

    // 유저 차단
    @Transactional
    public BlockResponse blockUser(Long blockerId, Long blockedId) {
        if (blockerId.equals(blockedId)) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        User blocker = userRepository.findById(blockerId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        User blocked = userRepository.findById(blockedId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 이미 차단된 경우 무시
        if (userBlockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)) {
            return new BlockResponse(true);
        }

        UserBlock userBlock = UserBlock.builder()
                .blocker(blocker)
                .blocked(blocked)
                .build();

        userBlockRepository.save(userBlock);
        return new BlockResponse(true);
    }

    // 차단 해제
    @Transactional
    public BlockResponse unblockUser(Long blockerId, Long blockedId) {
        UserBlock userBlock = userBlockRepository.findByBlockerIdAndBlockedId(blockerId, blockedId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        userBlockRepository.delete(userBlock);
        return new BlockResponse(false);
    }
}
