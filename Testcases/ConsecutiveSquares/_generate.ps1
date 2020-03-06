for ($i = 1; $i -le 100; $i++) {
    $rects = $i * 100
    $height = $rects
    $content = "container height: $height`n"
    $content += "rotations allowed: no`n"
    $content += "number of rectangles: $rects`n"
    for ($j = 1; $j -le $rects; $j++) {
        $content += "$j $j`n"
    }
    $content | Out-File -FilePath C:\Users\20182701\Documents\_algorithms_dbl\2IO90-DBL-Algorithms\Testcases\ConsecutiveSquaresFixed\consecutivesquares$rects-fixed$height.in
}